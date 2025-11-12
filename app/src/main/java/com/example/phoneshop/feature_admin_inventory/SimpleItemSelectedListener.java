package com.example.phoneshop.feature_admin_inventory;

import android.view.View;
import android.widget.AdapterView;

public class SimpleItemSelectedListener implements AdapterView.OnItemSelectedListener {
    private final Runnable onSelect;

    public SimpleItemSelectedListener(Runnable onSelect) {
        this.onSelect = onSelect;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (onSelect != null) onSelect.run();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) { }
}
