package edu.smarteye.sensing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;

import android.annotation.TargetApi;
import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.util.Log;
import edu.buffalo.cse.phonelab.lib.PeriodicTask;

@TargetApi(16)
public class ProximityTask extends PeriodicTask 
{
	private final String SERVICE_TYPE = "_http._tcp.";
	protected final Long DISCOVERY_INTERVAL_MS = 10000L;
	protected final Long REGISTER_INTERVAL_MS = 50000L;
	
	NsdServiceInfo serviceInfo;	
	NsdManager nsdManager;
	
	private boolean localServiceRunning;
	private String hashedID;
	private String serviceName;
	private HashSet<NsdServiceInfo> discoveredServices;
	private boolean discoveryStarted;
	
	@TargetApi(16)
	public ProximityTask(Context context, String logTag, Long interval) throws IOException, NoSuchAlgorithmException {
		super(context, logTag, interval);
				
		TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		MessageDigest digester = MessageDigest.getInstance("SHA-1");
		byte[] digest = digester.digest(telephonyManager.getDeviceId().getBytes());
		hashedID = (new BigInteger(1, digest)).toString(16);
		
		serviceName = hashedID;
		
		int port = new ServerSocket(0).getLocalPort();
		nsdManager = (NsdManager) context.getSystemService(Context.NSD_SERVICE);
		
		serviceInfo = new NsdServiceInfo();
		serviceInfo.setPort(port);
		//serviceInfo.setServiceName(serviceName);
		serviceInfo.setServiceType(SERVICE_TYPE);
		
		localServiceRunning = false;
		discoveryStarted = false;
		discoveredServices = new HashSet<NsdServiceInfo>();
	}

	@Override
	public synchronized void start() {
		super.start();
	}
	
	@Override
	public synchronized void stop() {
		super.stop();
		if (localServiceRunning == true)
		{
			nsdManager.unregisterService(registrationListener);
		}
	}
	
	@Override
	protected void check() {
		
		synchronized(this) {
			if (localServiceRunning == true)
			{
				Log.w(TAG, "Service already registered");
				return;
			}else {
					serviceName = hashedID;
					try
					{
						File root = Environment.getExternalStorageDirectory();
						File f= new File(root.getAbsolutePath(), "status.txt");
						BufferedReader br = new BufferedReader(new FileReader(f));
						String s = null;
						if ((s=br.readLine())!= null && (s = s.trim()).length() > 0)
							{
								serviceName = hashedID+s;
							}
						if (serviceName.contains("FLAG1") && !serviceName.equals(hashedID))
							{
								serviceInfo.setServiceName(serviceName);
								nsdManager.registerService(serviceInfo, NsdManager.PROTOCOL_DNS_SD, registrationListener);
								
							}
					}catch(Exception e){
						Log.e(TAG, e.getMessage());
						serviceName = hashedID;
					}
					
					
			}
		}
		
		synchronized (this) {
			if (discoveryStarted == true) {
				Log.w(TAG, "Discovery already running.");
				return;
			} else {
				discoveredServices.clear();
				nsdManager.discoverServices(SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, discoveryListener);
			}
		}
		
		try {
			Thread.sleep(DISCOVERY_INTERVAL_MS);
		} catch (InterruptedException e) {
			Log.w(TAG, "Interrupted waiting for discovery.");
		}
		
		synchronized (this) {
			if (discoveryStarted == false) {
				Log.w(TAG, "Discovery didn't start.");
				return;
			} else {
				nsdManager.stopServiceDiscovery(discoveryListener);
			}
		}
		
		try{
			if (localServiceRunning == true)
			{
				Thread.sleep(REGISTER_INTERVAL_MS);
			}
			
		}catch(InterruptedException e){
			Log.w(TAG, "Interrupted running local service");
		}
		
		synchronized(this){
			if (localServiceRunning == false)
			{
				Log.w(TAG, "Local service not registered");
				return;
			}else {
				nsdManager.unregisterService(registrationListener);
				
			}
			
		}
		
	}
	
	NsdManager.DiscoveryListener discoveryListener = new NsdManager.DiscoveryListener() {

		// @Override
		public void onDiscoveryStarted(String regType) {
			synchronized (ProximityTask.this) {
				discoveryStarted = true;
			}
			Log.d(TAG, "Service discovery started");
			
		}

		
		// @Override
		public void onDiscoveryStopped(String serviceType) {
			synchronized (ProximityTask.this) {
				discoveryStarted = false;
				//boolean localServiceRunning = false;
				Log.d(TAG, "# discovered services = "+discoveredServices.size());
				for (NsdServiceInfo service : discoveredServices) {
					if (service.getServiceName().contains(hashedID)) {
						//localServiceRunning = true;
						Log.v(TAG, "Discovered service: " + serviceInfo.getServiceName());
						continue;
					} else {
						nsdManager.resolveService(serviceInfo, resolveListener);
						Log.v(TAG, "ALert service: " + serviceInfo.getServiceName());
					}
				}
				/*if (localServiceRunning == true) {
					Log.i(TAG, "Local service running. Stoping.");
					//nsdManager.registerService(serviceInfo, NsdManager.PROTOCOL_DNS_SD, registrationListener);
					//nsdManager.unregisterService(registrationListener);
				}*/
			}
			Log.d(TAG, "Service discovery stopped.");
		}

		// @Override
		public void onStartDiscoveryFailed(String serviceType, int errorCode) {
			Log.w(TAG, "Failed to start discovery: " + errorCode);
			synchronized (ProximityTask.this) {
				if (discoveryStarted == true) {
					Log.e(TAG, "Discovery started should not be true.");
				}
			}
		}

		// @Override
		public void onStopDiscoveryFailed(String serviceType, int errorCode) {
			Log.w(TAG, "Failed to stop discovery: " + errorCode);
			synchronized (ProximityTask.this) {
				if (discoveryStarted == false) {
					Log.e(TAG, "Discovery started should not be true.");
				}
			}
		}
		// @Override
		public void onServiceFound(NsdServiceInfo service) {
			if (!discoveredServices.contains(service))
			{
				discoveredServices.add(service);
			}
		}

		// @Override
		public void onServiceLost(NsdServiceInfo service) {
		}
	};

	NsdManager.RegistrationListener registrationListener = new NsdManager.RegistrationListener() {

		// @Override
		public void onServiceRegistered(NsdServiceInfo nsdServiceInfo) {
			synchronized(ProximityTask.this)
			{
				Log.i(TAG, "Local service registered: "+nsdServiceInfo.getServiceName());
				localServiceRunning = true;
			}
		}

		// @Override
		public void onRegistrationFailed(NsdServiceInfo arg0, int arg1) {
		}

		// @Override
		public void onServiceUnregistered(NsdServiceInfo arg0) {
			synchronized(ProximityTask.this){
				Log.i(TAG, "Local service unregistered: "+arg0.getServiceName());
				localServiceRunning = false;
			}
		}

		// @Override
		public void onUnregistrationFailed(NsdServiceInfo serviceInfo,
				int errorCode) {
		}

	};
	
	NsdManager.ResolveListener resolveListener = new NsdManager.ResolveListener() {
		
		public void onServiceResolved(NsdServiceInfo serviceInfo) {
			synchronized (ProximityTask.this) {
				Log.i(TAG, "Resolved service " + serviceInfo.getServiceName());
			}
			
		}
		
		public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) { }
	};
}