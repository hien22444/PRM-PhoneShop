package com.example.phoneshop.feature_shopping;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.phoneshop.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

public class StoreMapFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private NavController navController;

    // Store location (Example: FPT University HCM)
    private static final LatLng STORE_LOCATION = new LatLng(10.8411, 106.8098);
    private static final String STORE_NAME = "PhoneShop - Cửa hàng chính";
    private static final String STORE_ADDRESS = "Lô E2a-7, Đường D1, Đ. D1, Long Thạnh Mỹ, Thành Phố Thủ Đức, Hồ Chí Minh";
    private static final String STORE_PHONE = "0123 456 789";
    private static final String STORE_HOURS = "8:00 - 22:00 (Hàng ngày)";

    // Views
    private MaterialToolbar toolbar;
    private MaterialCardView cardStoreInfo;
    private TextView tvStoreName;
    private TextView tvStoreAddress;
    private TextView tvStorePhone;
    private TextView tvStoreHours;
    private MaterialButton btnDirections;
    private MaterialButton btnCall;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_store_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);

        bindViews(view);
        setupListeners();
        setupMap();
    }

    private void bindViews(View view) {
        toolbar = view.findViewById(R.id.toolbar);
        cardStoreInfo = view.findViewById(R.id.cardStoreInfo);
        tvStoreName = view.findViewById(R.id.tvStoreName);
        tvStoreAddress = view.findViewById(R.id.tvStoreAddress);
        tvStorePhone = view.findViewById(R.id.tvStorePhone);
        tvStoreHours = view.findViewById(R.id.tvStoreHours);
        btnDirections = view.findViewById(R.id.btnDirections);
        btnCall = view.findViewById(R.id.btnCall);

        // Set store info
        tvStoreName.setText(STORE_NAME);
        tvStoreAddress.setText(STORE_ADDRESS);
        tvStorePhone.setText("📞 " + STORE_PHONE);
        tvStoreHours.setText("🕐 " + STORE_HOURS);
    }

    private void setupListeners() {
        toolbar.setNavigationOnClickListener(v -> navController.navigateUp());

        btnDirections.setOnClickListener(v -> openDirections());
        btnCall.setOnClickListener(v -> makePhoneCall());
    }

    private void setupMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.mapFragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // Add marker at store location
        mMap.addMarker(new MarkerOptions()
                .position(STORE_LOCATION)
                .title(STORE_NAME)
                .snippet(STORE_ADDRESS));

        // Move camera to store location
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(STORE_LOCATION, 15f));

        // Enable zoom controls
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);

        // Enable my location if permission granted
        if (ActivityCompat.checkSelfPermission(requireContext(), 
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
        } else {
            // Request permission
            requestPermissions(
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    100
            );
        }

        // Set map type
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
    }

    private void openDirections() {
        // Open Google Maps with directions
        String uri = String.format("google.navigation:q=%f,%f", 
                STORE_LOCATION.latitude, STORE_LOCATION.longitude);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        intent.setPackage("com.google.android.apps.maps");
        
        if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {
            startActivity(intent);
        } else {
            // If Google Maps not installed, open in browser
            String browserUri = String.format("https://www.google.com/maps/dir/?api=1&destination=%f,%f",
                    STORE_LOCATION.latitude, STORE_LOCATION.longitude);
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(browserUri));
            startActivity(browserIntent);
        }
    }

    private void makePhoneCall() {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + STORE_PHONE));
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, 
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        
        if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, enable my location
                if (mMap != null && ActivityCompat.checkSelfPermission(requireContext(),
                        Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    mMap.setMyLocationEnabled(true);
                    mMap.getUiSettings().setMyLocationButtonEnabled(true);
                }
            } else {
                Toast.makeText(getContext(), 
                        "Cần cấp quyền truy cập vị trí để sử dụng tính năng này", 
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}

