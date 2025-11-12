package com.example.phoneshop.feature_admin_inventory;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.phoneshop.R;
import com.example.phoneshop.data.model.Product;
// KHÔNG CẦN import RetrofitClient ở đây nữa
import com.example.phoneshop.data.repository.ProductRepository;

import java.util.ArrayList;
import java.util.List;

// KHÔNG CẦN import Retrofit Callbacks ở đây nữa

public class AdminAddEditProductFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1001;

    private EditText edtName, edtBrand, edtPrice, edtStock;
    private ImageView imgPicked;
    private Button btnPickImage, btnSave;

    private String editingId = null;
    private Product editingProduct = null;

    // LỖI 1: Sửa cách lấy Repository
    private ProductRepository repo; // Không khởi tạo ở đây

    private final List<String> selectedImages = new ArrayList<>();
    private Uri selectedImageUri;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_admin_add_edit_product, container, false);

        // LẤY REPO INSTANCE TRONG ONCREATEVIEW
        repo = ProductRepository.getInstance();

        edtName = v.findViewById(R.id.edtName);
        edtBrand = v.findViewById(R.id.edtBrand);
        edtPrice = v.findViewById(R.id.edtPrice);
        edtStock = v.findViewById(R.id.edtStock);
        imgPicked = v.findViewById(R.id.imgPicked);
        btnPickImage = v.findViewById(R.id.btnPickImage);
        btnSave = v.findViewById(R.id.btnSave);

        // Nhận id nếu là sửa
        if (getArguments() != null) {
            editingId = getArguments().getString("id");
            if (editingId != null) loadProduct(editingId);
        }

        btnPickImage.setOnClickListener(v1 -> openGallery());
        btnSave.setOnClickListener(v1 -> saveProduct());

        return v;
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int req, int res, @Nullable Intent data) {
        super.onActivityResult(req, res, data);
        if (req == PICK_IMAGE_REQUEST && res == Activity.RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                imgPicked.setImageURI(selectedImageUri);
                selectedImages.clear();
                selectedImages.add(selectedImageUri.toString());
                Toast.makeText(requireContext(), "Đã chọn ảnh", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void loadProduct(String id) {
        // LỖI 2: Sửa từ .enqueue() thành .observe()
        repo.getProductById(id).observe(getViewLifecycleOwner(), product -> {
            if (product == null) {
                Toast.makeText(requireContext(), "Không tải được dữ liệu", Toast.LENGTH_SHORT).show();
                return;
            }
            editingProduct = product;
            edtName.setText(editingProduct.getName());
            edtBrand.setText(editingProduct.getBrand());
            edtPrice.setText(String.valueOf(editingProduct.getPrice()));
            edtStock.setText(String.valueOf(editingProduct.getStock()));

            if (editingProduct.getImages() != null && !editingProduct.getImages().isEmpty()) {
                Glide.with(requireContext())
                        .load(editingProduct.getImages().get(0))
                        .placeholder(R.drawable.ic_image_placeholder)
                        .into(imgPicked);
            } else {
                imgPicked.setImageResource(R.drawable.ic_image_placeholder);
            }
        });
    }

    private void saveProduct() {
        String name = edtName.getText().toString().trim();
        String brand = edtBrand.getText().toString().trim();
        String priceText = edtPrice.getText().toString().trim();
        String stockText = edtStock.getText().toString().trim();

        if (name.isEmpty() || brand.isEmpty() || priceText.isEmpty() || stockText.isEmpty()) {
            Toast.makeText(requireContext(), "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        Product p = new Product();
        p.setName(name);
        p.setBrand(brand);
        // LỖI 3: Sửa ép kiểu (long)
        p.setPrice((long) Double.parseDouble(priceText));
        p.setStock(Integer.parseInt(stockText));

        // Giữ ảnh cũ nếu không chọn lại
        if (!selectedImages.isEmpty()) {
            p.setImages(selectedImages);
        } else if (editingProduct != null && editingProduct.getImages() != null) {
            p.setImages(editingProduct.getImages());
        }

        if (editingId == null) {
            // LỖI 4: Sửa cách gọi hàm create
            repo.createProduct(p).observe(getViewLifecycleOwner(), createdProduct -> {
                if (createdProduct != null) {
                    Toast.makeText(requireContext(), "Đã thêm sản phẩm", Toast.LENGTH_SHORT).show();
                    if (getActivity() != null) getActivity().getSupportFragmentManager().popBackStack();
                } else {
                    Toast.makeText(requireContext(), "Thêm thất bại", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // LỖI 5: Sửa cách gọi hàm update
            repo.updateProduct(editingId, p).observe(getViewLifecycleOwner(), updatedProduct -> {
                if (updatedProduct != null) {
                    Toast.makeText(requireContext(), "Đã cập nhật sản phẩm", Toast.LENGTH_SHORT).show();
                    if (getActivity() != null) getActivity().getSupportFragmentManager().popBackStack();
                } else {
                    Toast.makeText(requireContext(), "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}