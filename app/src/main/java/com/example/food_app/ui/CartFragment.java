package com.example.food_app.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.food_app.models.Address;
import com.example.food_app.adapters.CartAdapter;
import com.example.food_app.models.CartItem;
import com.example.food_app.DatabaseHelper;
import com.example.food_app.GPSTracker;
import com.example.food_app.HistoryFragment;
import com.example.food_app.NetworkChangeReceiver;
import com.example.food_app.Order;
import com.example.food_app.R;
import com.example.food_app.GlobalVariable;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CartFragment extends Fragment implements CartAdapter.CartAdapterListener {

    private MapView mapView;
    private static final int REQUEST_CODE_PERMISSION = 2;
    String mPermission = Manifest.permission.ACCESS_FINE_LOCATION;
    // GPSTracker class
    GPSTracker gps;
    double latitude;
    double longitude;
    private Marker marker;
    private Circle circle;
    private LatLng selectedLocation;
    private RecyclerView cartRecyclerView;
    private CartAdapter cartAdapter;
    private DatabaseHelper dbHelper;
    private TextView totalPrice;
    private Button btnConfirmOrder;
    EditText cityEditText, buildingEditText, streetEditText, floorEditText, detailsEditText;
    public GlobalVariable glob;
    private TextView locationTextView;
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 0;
    private NetworkChangeReceiver networkChangeReceiver;
    private DatabaseReference userRef;

    @Override
    public void onCartItemChanged() {
        updateTotalPrice(); // Update the total price whenever a change occurs
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_cart, container, false);

        dbHelper = new DatabaseHelper(requireContext());

        cartRecyclerView = rootView.findViewById(R.id.shoppingcart);
        cartRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        userRef = FirebaseDatabase.getInstance("https://bitemate-c316c-default-rtdb.europe-west1.firebasedatabase.app/").getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        // Retrieve the cart items from the database
        List<CartItem> cartItems = dbHelper.getCartItems();

        // Create and set the adapter for the RecyclerView
        cartAdapter = new CartAdapter(cartItems, this);
        cartRecyclerView.setAdapter(cartAdapter);

        totalPrice = rootView.findViewById(R.id.totalPrice);
        updateTotalPrice(); // Add this line to initialize the total price

        btnConfirmOrder = rootView.findViewById(R.id.btn_ConfirmOder);
        btnConfirmOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Get the total price
                double total = dbHelper.getTotalPrice();

                if (total == 0) {
                    Toast.makeText(requireContext(), "Failed : Cart is empty", Toast.LENGTH_SHORT).show();
                } else if(!NetworkChangeReceiver.networkStatus){
                    Toast.makeText(requireContext(), "No network connection. Please connect to the internet.", Toast.LENGTH_SHORT).show();
                }else{
                    // Get the address information
                    cityEditText = rootView.findViewById(R.id.cityEditText);
                    buildingEditText = rootView.findViewById(R.id.buildingEditText);
                    streetEditText = rootView.findViewById(R.id.streetEditText);
                    floorEditText = rootView.findViewById(R.id.floorEditText);
                    detailsEditText = rootView.findViewById(R.id.detailsEditText);
                    String city = cityEditText.getText().toString().trim();
                    String building = buildingEditText.getText().toString().trim();
                    String street = streetEditText.getText().toString().trim();
                    String floor = floorEditText.getText().toString().trim();
                    //String details = detailsEditText.getText().toString().trim();

                    // Check if the required fields are empty
                    if (TextUtils.isEmpty(city) || TextUtils.isEmpty(building) || TextUtils.isEmpty(street) || TextUtils.isEmpty(floor)) {
                        Toast.makeText(requireContext(), "Failed: Please enter all address information", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Get the cart items
                    List<CartItem> cartItems = dbHelper.getCartItems();
                   // Toast.makeText(requireContext(), (CharSequence) cartItems.get(0), Toast.LENGTH_SHORT).show();


                    Address address = new Address();
                    address.setCity(city);
                    address.setBuilding(building);
                    address.setStreet(street);
                    address.setFloor(floor);
                    address.setCoordinates(latitude + " | " + longitude);


                    userRef.child("points").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                int currentPoints = snapshot.getValue(Integer.class);

                                // Check if total > 20 and add additional points if necessary
                                if (total > 60) {
                                    currentPoints += 30;
                                } else if (total > 40) {
                                    currentPoints += 20;
                                } else if (total > 20) {
                                    currentPoints += 10;
                                }

                                // Update the points value in the database
                                userRef.child("points").setValue(currentPoints);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }

                    });

                    // Create a new order object with the extracted data
                    String orderId = FirebaseDatabase.getInstance("https://bitemate-c316c-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("orders").push().getKey();
                    Order order = new Order(orderId, glob.currentUserId, address, cartItems, total, getCurrentDate());

                    // Save the order to Firebase
                    DatabaseReference ordersRef = FirebaseDatabase.getInstance("https://bitemate-c316c-default-rtdb.europe-west1.firebasedatabase.app/").getReference().child("orders");
                    ordersRef.child(orderId).setValue(order);


                    // Clear the cart and update the UI
                    dbHelper.deleteCartItems(glob.currentUserId);
                    cartItems.clear();
                    cartAdapter.notifyDataSetChanged();
                    updateTotalPrice();


                    // Send SMS to the user
                    if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.SEND_SMS}, MY_PERMISSIONS_REQUEST_SEND_SMS);
                    } else {
                        sendOrderConfirmationSMS();
                    }

                    getParentFragmentManager().beginTransaction()
                            .replace(R.id.fragmentContainerView2, HistoryFragment.class, null)
                            .setReorderingAllowed(true)
                            .addToBackStack(null)
                            .commit();

                }
            }
        });




        try {
            PackageManager pManager = getActivity().getPackageManager();
            if (ActivityCompat.checkSelfPermission(requireContext(), mPermission)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(requireActivity(), new String[]{mPermission},
                        REQUEST_CODE_PERMISSION);

                // If any permission above not allowed by user, this condition will
                // execute every time, else your else part will work
            }
        } catch (Exception e) {
            e.printStackTrace();
        }



        gps = new GPSTracker(requireContext());

        // check if GPS enabled
        if (gps.canGetLocation()) {

            latitude = gps.getLatitude();
            longitude = gps.getLongitude();

        } else {
            //location of beirut
            latitude = 33.888630;
            longitude = 35.495480;
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            gps.showSettingsAlert();
        }

        mapView = rootView.findViewById(R.id.mapView2);

        mapView.onCreate(savedInstanceState);

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                // Set the map type to hybrid
                googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

                // Set the initial camera position to Beirut
                LatLng userLocation = new LatLng(latitude, longitude);
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(userLocation)
                        .zoom(15)
                        .build();
                googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @SuppressLint("MissingInflatedId")
                    @Override
                    public void onMapClick(LatLng latLng) {

                        // Remove any existing marker
                        if (marker != null) {
                            marker.remove();
                        }

                        // Add a new marker at the selected location
                        marker = googleMap.addMarker(new MarkerOptions()
                                .position(latLng)
                                .title("Selected Location"));

                        CircleOptions circleOptions = new CircleOptions()
                                .center(latLng)
                                .center(latLng)
                                .radius(50) // in meters
                                .strokeColor(Color.RED)
                                .fillColor(Color.argb(64, 255, 0, 0));
                        if (circle != null) {
                            circle.remove();
                        }
                        circle = googleMap.addCircle(circleOptions);

                        // Set the selected location
                        selectedLocation = latLng;

                        latitude = latLng.latitude;
                        longitude = latLng.longitude;
                        locationTextView = rootView.findViewById(R.id.locationTextCoord);
                        locationTextView.setText("location coordinates saved (" + longitude + " , " + latitude + ")");

                    }
                });
            }
        });

        return rootView;
    }


    private void updateTotalPrice() {
        double total = dbHelper.getTotalPrice();
        totalPrice.setText(String.format("$%.2f", total));
    }

    // Helper method to get the current date in the required format
    private String getCurrentDate() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return dateFormat.format(new Date());
    }


    // Method to send the order confirmation SMS
    private void sendOrderConfirmationSMS() {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String phone=sharedPreferences.getString("phoneNumber", "");
        String message = "Hello, Thank you for using BiteMate. /n" +
                "Your order is on it's way!! ";

        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phone, null, message, null, null);
            Toast.makeText(requireContext(), "Order confirmation SMS sent", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(requireContext(), "Failed to send order confirmation SMS", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_SEND_SMS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                sendOrderConfirmationSMS();
            } else {
                Toast.makeText(requireContext(), "Permission denied. Unable to send SMS.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();

        // Initialize and register the BroadcastReceiver
        networkChangeReceiver = new NetworkChangeReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        requireActivity().registerReceiver(networkChangeReceiver, filter);
    }

    @Override
    public void onPause() {
        mapView.onPause();
        super.onPause();

        // Unregister the BroadcastReceiver
        requireActivity().unregisterReceiver(networkChangeReceiver);
    }

    @Override
    public void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }
    @Override
    public void onLowMemory() {
        mapView.onLowMemory();
        super.onLowMemory();
    }

}

