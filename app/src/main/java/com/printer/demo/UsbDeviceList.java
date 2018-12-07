package com.printer.demo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.printer.sdk.usb.USBPort;

/**
 * This Activity appears as a dialog. It lists any paired devices and devices
 * detected in the area after discovery. When a device is chosen by the user,
 * the MAC address of the device is sent back to the parent Activity in the
 * result Intent.
 *
 * @param <V>
 * @param <E>
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
public class UsbDeviceList<V> extends Activity {
	private ArrayAdapter<String> deviceArrayAdapter;
	private ListView mFoundDevicesListView;
	private Button scanButton;
	private Button backButton;
	private List<UsbDevice> deviceList;
	private HashMap<String, UsbDevice> devices;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.device_list);
		setTitle(R.string.select_device);

		setResult(Activity.RESULT_CANCELED);

		scanButton = (Button) findViewById(R.id.button_scan);
		scanButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				doDiscovery();
			}
		});

		backButton= (Button) findViewById(R.id.button_bace);
		backButton.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
                 finish();
			}

		});

		deviceArrayAdapter = new ArrayAdapter<String>(this,
				R.layout.device_item);
		deviceList = new ArrayList<UsbDevice>();

		mFoundDevicesListView = (ListView) findViewById(R.id.paired_devices);
		mFoundDevicesListView.setAdapter(deviceArrayAdapter);
		mFoundDevicesListView.setOnItemClickListener(mDeviceClickListener);
//		doDiscovery();
	}


	private void doDiscovery() {
		Log.d("deviceArrayAdapter",deviceArrayAdapter.getCount()+"");
		Log.d("deviceList",deviceList.size()+"");
		deviceArrayAdapter.clear();
		deviceList.clear();
		UsbManager manager = (UsbManager) getSystemService(Context.USB_SERVICE);
		devices= manager.getDeviceList();

		for (UsbDevice device : devices.values()) {
			if (USBPort.isUsbPrinter(device)) {
				deviceArrayAdapter.add(device.getDeviceName() + "\nvid: "
						+ device.getVendorId() + " pid: "
						+ device.getProductId());
//				Log.d("UsbDeviceList Add",device.getManufacturerName());
				deviceList.add(device);
			}
		}
	}

	private void returnToPreviousActivity(UsbDevice device) {
		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		bundle.putParcelable(UsbManager.EXTRA_DEVICE, device);
		intent.putExtras(bundle);
		setResult(Activity.RESULT_OK, intent);
		finish();
	}

	private OnItemClickListener mDeviceClickListener = new OnItemClickListener() {
		public void onItemClick(AdapterView<?> av, View v, int position, long id) {
			returnToPreviousActivity(deviceList.get(position));
		}
	};
}
