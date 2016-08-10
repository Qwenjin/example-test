package com.example.baidumaptest;

import java.util.List;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends BaseActivity {
	private MapView mapView;
	private BaiduMap baiduMap;
	private LocationManager locationManeger;
	private String provider;
	private boolean isFirstLocate = true;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SDKInitializer.initialize(getApplicationContext());
		setContentView(R.layout.activity_main);
		mapView = (MapView) findViewById(R.id.map_view);
		
		//
		baiduMap = mapView.getMap();
		locationManeger = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		baiduMap.setMyLocationEnabled(true);
		
		//获取所有可用的位置提供器
		List<String> providerList = locationManeger.getProviders(true);
		if( providerList.contains(LocationManager.GPS_PROVIDER))
		{
			
			provider = LocationManager.GPS_PROVIDER;
			
		}else if( providerList.contains(LocationManager.NETWORK_PROVIDER) )
		{
			provider = LocationManager.NETWORK_PROVIDER;
		}else {
			//当前没有可用的位置选择器， 弹出Toast提示用户
			Toast.makeText(this, "No location provider to use", Toast.LENGTH_SHORT).show();
			return ;
		}
		
		Location location = locationManeger.getLastKnownLocation(provider);
		if(location != null)
		{
			navigateTo(location);
		}
		locationManeger.requestLocationUpdates(provider,  5000, 1, locationListener);
	}
	
	private void navigateTo(Location location)
	{
		if(isFirstLocate)
		{
			LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
			MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(ll);
			baiduMap.animateMapStatus(update);
			
			update = MapStatusUpdateFactory.zoomTo(16f);
			baiduMap.animateMapStatus(update);
			
			isFirstLocate = false;
		}
		
		MyLocationData.Builder locationBuilder = new MyLocationData.Builder();
		locationBuilder.latitude(location.getLatitude());
		locationBuilder.longitude(location.getLongitude());
		MyLocationData locationData = locationBuilder.build();
		
		baiduMap.setMyLocationData(locationData);
		
	}//navigateTo
	LocationListener locationListener = new LocationListener() {
		
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			
		}
		
		@Override
		public void onProviderEnabled(String provider) {
			
		}
		
		@Override
		public void onProviderDisabled(String provider) {
			
		}
		
		@Override
		public void onLocationChanged(Location location) {
			//更新当前设备的位置信息
			if(location != null)
			{
				navigateTo(location);
			}
			
		}
	};
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mapView.onDestroy();
		baiduMap.setMyLocationEnabled(false);
		if(locationManeger != null)
		{
			//关闭程序时将监听器移除
			locationManeger.removeUpdates(locationListener);
		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		mapView.onPause();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		mapView.onResume();
	}

}
