package com.example.phoneshop.feature_admin_inventory;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import com.example.phoneshop.R;
import com.example.phoneshop.common.utils.ItemOffsetDecoration;
import com.example.phoneshop.common.utils.UiState;
import com.example.phoneshop.data.model.Product;
import com.example.phoneshop.feature_admin_inventory.adapters.AdminProductAdapter;

import java.util.Arrays;
import java.util.List;

public class AdminProductListFragment extends Fragment {
    private AdminInventoryViewModel vm;
    private AdminProductAdapter adapter;
    private SwipeRefreshLayout swipe;
    private TextView tvEmpty;
    private Spinner ddBrand, ddSort;

    private boolean isSpinnerInitialized = false; // Tránh trigger refresh khi khởi tạo

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.e("AdminProductListFrag", "onCreateView() được gọi");
        return inflater.inflate(R.layout.fragment_admin_product_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle s) {
        super.onViewCreated(v, s);

        Log.e("AdminProductListFrag", "==========================================");
        Log.e("AdminProductListFrag", "onViewCreated() ĐƯỢC GỌI!");
        Log.e("AdminProductListFrag", "==========================================");

        // CÁCH 1: ViewModelProvider (cách chuẩn)
        Log.e("AdminProductListFrag", "Thử cách 1: ViewModelProvider");
        try {
            ViewModelProvider provider = new ViewModelProvider(this);
            vm = provider.get(AdminInventoryViewModel.class);
            Log.e("AdminProductListFrag", "✓ ViewModel from provider: " + vm);
            Log.e("AdminProductListFrag", "✓ ViewModel hashCode: " + vm.hashCode());
        } catch (Exception e) {
            Log.e("AdminProductListFrag", "❌ ViewModelProvider failed: " + e.getMessage(), e);

            // CÁCH 2: Tạo thủ công (fallback)
            Log.e("AdminProductListFrag", "Fallback: Tạo ViewModel thủ công");
            vm = new AdminInventoryViewModel();
            Log.e("AdminProductListFrag", "✓ Manual ViewModel: " + vm);
        }

        // Bind views
        RecyclerView rv = v.findViewById(R.id.rvProducts);
        swipe = v.findViewById(R.id.swipe);
        tvEmpty = v.findViewById(R.id.tvEmpty);
        SearchView search = v.findViewById(R.id.searchView);
        FloatingActionButton fabAdd = v.findViewById(R.id.fabAdd);
        ddBrand = v.findViewById(R.id.ddBrand);
        ddSort = v.findViewById(R.id.ddSort);

        Log.e("AdminProductListFrag", "✓ Tất cả views đã được findViewById");

        // Setup Adapter
        adapter = new AdminProductAdapter(new AdminProductAdapter.Listener() {
            @Override
            public void onToggleVisible(Product p, boolean newState) {
                vm.toggleVisible(p, newState);
            }

            @Override
            public void onEdit(Product p) {
                Bundle b = new Bundle();
                b.putString("id", p.getId());
                AdminAddEditProductFragment f = new AdminAddEditProductFragment();
                f.setArguments(b);

                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.admin_nav_host_fragment, f)
                        .addToBackStack("add_edit")
                        .commit();
            }

            @Override
            public void onDelete(Product p) {
                new MaterialAlertDialogBuilder(requireContext())
                        .setTitle("Xóa sản phẩm?")
                        .setMessage("Bạn có chắc muốn xóa \"" + p.getName() + "\" không?")
                        .setNegativeButton("Không", null)
                        .setPositiveButton("Xóa", (dialog, which) -> {
                            vm.deleteProduct(p.getId(),
                                    () -> Snackbar.make(requireView(), "Đã xóa", Snackbar.LENGTH_SHORT).show(),
                                    () -> Snackbar.make(requireView(), "Xóa thất bại", Snackbar.LENGTH_SHORT).show());
                        })
                        .show();
            }
        });

        rv.setLayoutManager(new LinearLayoutManager(requireContext()));
        rv.addItemDecoration(new ItemOffsetDecoration(8));
        rv.setAdapter(adapter);

        Log.e("AdminProductListFrag", "✓ RecyclerView setup xong");

        // Setup Spinners (KHÔNG gán listener ngay)
        List<String> brands = Arrays.asList("Tất cả", "Samsung", "Apple", "Xiaomi", "OPPO");
        ddBrand.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, brands));

        List<String> sorts = Arrays.asList("Mặc định", "Giá tăng dần", "Giá giảm dần");
        ddSort.setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, sorts));

        Log.e("AdminProductListFrag", "✓ Spinners đã setup xong");

        // Đăng ký Observer TRƯỚC KHI refresh
        Log.e("AdminProductListFrag", "Đăng ký observer...");
        observe();
        Log.e("AdminProductListFrag", "✓ Observer đã được đăng ký");

        // GỌI REFRESH
        Log.e("AdminProductListFrag", "==========================================");
        Log.e("AdminProductListFrag", "Chuẩn bị gọi vm.refresh()");
        Log.e("AdminProductListFrag", "ViewModel instance: " + vm);
        Log.e("AdminProductListFrag", "ViewModel hashCode: " + vm.hashCode());
        Log.e("AdminProductListFrag", "==========================================");

        vm.refresh();

        Log.e("AdminProductListFrag", "✓ Đã gọi vm.refresh()");

        // Setup Swipe Refresh
        swipe.setOnRefreshListener(() -> {
            Log.e("AdminProductListFrag", "SwipeRefresh triggered");
            vm.setSearch(search.getQuery().toString());
            vm.refresh();
        });

        // Setup SearchView
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String q) {
                Log.e("AdminProductListFrag", "Search query: " + q);
                vm.setSearch(q);
                vm.refresh();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        // Setup FAB
        fabAdd.setOnClickListener(v1 -> {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.admin_nav_host_fragment, new AdminAddEditProductFragment())
                    .addToBackStack("add_edit")
                    .commit();
        });

        // Setup Spinner Listeners SAU KHI refresh
        // Dùng flag để tránh trigger refresh khi khởi tạo
        ddBrand.post(() -> {
            ddBrand.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (!isSpinnerInitialized) {
                        isSpinnerInitialized = true;
                        Log.e("AdminProductListFrag", "Brand spinner khởi tạo, bỏ qua lần đầu");
                        return;
                    }

                    String sel = (String) parent.getItemAtPosition(position);
                    Log.e("AdminProductListFrag", "Brand selected: " + sel);
                    vm.setBrand("Tất cả".equals(sel) ? null : sel);
                    vm.refresh();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            });
        });

        ddSort.post(() -> {
            ddSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                private boolean isFirstSelection = true;

                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (isFirstSelection) {
                        isFirstSelection = false;
                        Log.e("AdminProductListFrag", "Sort spinner khởi tạo, bỏ qua lần đầu");
                        return;
                    }

                    String sel = (String) parent.getItemAtPosition(position);
                    Log.e("AdminProductListFrag", "Sort selected: " + sel);

                    if ("Giá tăng dần".equals(sel)) {
                        vm.setSort("price,asc");
                    } else if ("Giá giảm dần".equals(sel)) {
                        vm.setSort("price,desc");
                    } else {
                        vm.setSort(null);
                    }
                    vm.refresh();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            });
        });

        Log.e("AdminProductListFrag", "✓ Setup hoàn tất");
    }

    private void observe() {
        Log.e("AdminProductListFrag", "observe() - đăng ký observer cho products LiveData");

        vm.products.observe(getViewLifecycleOwner(), state -> {
            Log.e("AdminProductListFrag", "===== OBSERVER TRIGGERED =====");
            Log.e("AdminProductListFrag", "State null? " + (state == null));

            if (state == null) {
                Log.w("AdminProductListFrag", "❌ State is NULL!");
                return;
            }

            Log.e("AdminProductListFrag", "Status: " + state.status);
            Log.e("AdminProductListFrag", "Data null? " + (state.data == null));

            if (state.data != null && state.data.content != null) {
                Log.e("AdminProductListFrag", "Data content size: " + state.data.content.size());
            }

            // Update SwipeRefreshLayout
            swipe.setRefreshing(state.status == UiState.Status.LOADING);

            // Handle different states
            if (state.status == UiState.Status.SUCCESS && state.data != null) {
                Log.e("AdminProductListFrag", "✓✓✓ SUCCESS - Content size: " + state.data.content.size());

                adapter.submit(state.data.content);
                tvEmpty.setVisibility(state.data.content.isEmpty() ? View.VISIBLE : View.GONE);

                if (state.data.content.isEmpty()) {
                    tvEmpty.setText("Không có sản phẩm nào");
                }

            } else if (state.status == UiState.Status.EMPTY) {
                Log.e("AdminProductListFrag", "⚠ EMPTY STATE");
                adapter.submit(null);
                tvEmpty.setText("Không có sản phẩm nào");
                tvEmpty.setVisibility(View.VISIBLE);

            } else if (state.status == UiState.Status.ERROR) {
                Log.e("AdminProductListFrag", "❌ ERROR STATE: " + state.error);
                tvEmpty.setText("Lỗi: " + (state.error != null ? state.error : "Unknown error"));
                tvEmpty.setVisibility(View.VISIBLE);

                // Show Snackbar for error
                Snackbar.make(requireView(), "Lỗi: " + state.error, Snackbar.LENGTH_LONG).show();

            } else if (state.status == UiState.Status.LOADING) {
                Log.e("AdminProductListFrag", "⏳ LOADING STATE");
                tvEmpty.setVisibility(View.GONE);
            }
        });

        Log.e("AdminProductListFrag", "✓ Observer đã được đăng ký xong");
    }
}