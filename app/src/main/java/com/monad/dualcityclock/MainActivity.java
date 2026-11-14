package com.monad.dualcityclock;

import android.app.Activity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    private Spinner firstCity, secondCity, displayLayout;
    private TextView firstLabel, secondLabel, sizeLabel;
    private TextClock firstClock, secondClock;
    private LinearLayout previewContainer, firstPanel, secondPanel;
    private View separator;
    private SeekBar fontSeek;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(0xFF091323);
        getWindow().setNavigationBarColor(0xFF091323);
        setContentView(R.layout.activity_main);

        firstCity = findViewById(R.id.first_spinner);
        secondCity = findViewById(R.id.second_spinner);
        displayLayout = findViewById(R.id.layout_spinner);
        firstLabel = findViewById(R.id.preview_city_left);
        secondLabel = findViewById(R.id.preview_city_right);
        firstClock = findViewById(R.id.preview_clock_left);
        secondClock = findViewById(R.id.preview_clock_right);
        previewContainer = findViewById(R.id.preview_container);
        firstPanel = findViewById(R.id.preview_panel_left);
        secondPanel = findViewById(R.id.preview_panel_right);
        separator = findViewById(R.id.preview_divider);
        fontSeek = findViewById(R.id.font_seek);
        sizeLabel = findViewById(R.id.font_size_label);

        ArrayAdapter<CityCatalog.City> adapter = new ArrayAdapter<>(this,
            android.R.layout.simple_spinner_item, CityCatalog.CITIES);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        firstCity.setAdapter(adapter);
        secondCity.setAdapter(adapter);
        firstCity.setSelection(CityCatalog.firstIndex(this));
        secondCity.setSelection(CityCatalog.secondIndex(this));
        ArrayAdapter<String> layouts = new ArrayAdapter<>(this,
            android.R.layout.simple_spinner_item,
            new String[]{"Stacked — largest clocks", "Side by side — compact"});
        layouts.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        displayLayout.setAdapter(layouts);
        displayLayout.setSelection(ClockSettings.stacked(this) ? 0 : 1);

        fontSeek.setMax((ClockSettings.MAX_SP - ClockSettings.MIN_SP) / 2);
        fontSeek.setProgress((ClockSettings.fontSize(this) - ClockSettings.MIN_SP) / 2);
        fontSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar bar, int value, boolean fromUser) {
                refreshPreview();
            }
            @Override public void onStartTrackingTouch(SeekBar bar) {}
            @Override public void onStopTrackingTouch(SeekBar bar) {}
        });
        AdapterView.OnItemSelectedListener onChange = new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                refreshPreview();
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        };
        firstCity.setOnItemSelectedListener(onChange);
        secondCity.setOnItemSelectedListener(onChange);
        displayLayout.setOnItemSelectedListener(onChange);

        findViewById(R.id.swap_button).setOnClickListener(v -> {
            int a = firstCity.getSelectedItemPosition();
            int b = secondCity.getSelectedItemPosition();
            firstCity.setSelection(b);
            secondCity.setSelection(a);
        });
        findViewById(R.id.save_button).setOnClickListener(v -> {
            int a = firstCity.getSelectedItemPosition();
            int b = secondCity.getSelectedItemPosition();
            if (a == b) {
                Toast.makeText(this, "Choose two different cities.", Toast.LENGTH_SHORT).show();
                return;
            }
            CityCatalog.save(this, a, b);
            ClockSettings.save(this, requestedSize(), isStacked());
            DualClockWidgetProvider.updateAll(this);
            Toast.makeText(this, "Widget updated.", Toast.LENGTH_SHORT).show();
        });
        refreshPreview();
    }

    private boolean isStacked() { return displayLayout.getSelectedItemPosition() == 0; }
    private int requestedSize() { return ClockSettings.MIN_SP + fontSeek.getProgress() * 2; }
    private int dp(float dp) { return Math.round(dp * getResources().getDisplayMetrics().density); }

    private void refreshPreview() {
        if (firstCity.getSelectedItemPosition() < 0 || secondCity.getSelectedItemPosition() < 0 ||
            displayLayout.getSelectedItemPosition() < 0) return;
        CityCatalog.City first = CityCatalog.CITIES[firstCity.getSelectedItemPosition()];
        CityCatalog.City second = CityCatalog.CITIES[secondCity.getSelectedItemPosition()];
        firstLabel.setText(first.label);
        secondLabel.setText(second.label);
        firstClock.setTimeZone(first.zone);
        secondClock.setTimeZone(second.zone);
        sizeLabel.setText("Clock size: " + requestedSize() + " sp");
        boolean stacked = isStacked();
        previewContainer.setOrientation(stacked ? LinearLayout.VERTICAL : LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams frame = (LinearLayout.LayoutParams) previewContainer.getLayoutParams();
        frame.height = dp(stacked ? 218 : 152);
        previewContainer.setLayoutParams(frame);
        if (stacked) {
            firstPanel.setLayoutParams(new LinearLayout.LayoutParams(-1, 0, 1));
            secondPanel.setLayoutParams(new LinearLayout.LayoutParams(-1, 0, 1));
            separator.setLayoutParams(new LinearLayout.LayoutParams(-1, dp(1)));
        } else {
            firstPanel.setLayoutParams(new LinearLayout.LayoutParams(0, -1, 1));
            secondPanel.setLayoutParams(new LinearLayout.LayoutParams(0, -1, 1));
            separator.setLayoutParams(new LinearLayout.LayoutParams(dp(1), -1));
        }
        previewContainer.post(() -> {
            float density = getResources().getDisplayMetrics().density;
            float w = (stacked ? firstPanel.getWidth() : firstPanel.getWidth() - dp(6)) / density;
            float h = (firstPanel.getHeight() - dp(25)) / density;
            float font = ClockSettings.fittedSp(this, requestedSize(), Math.max(w, 40), Math.max(h, 30));
            firstClock.setTextSize(TypedValue.COMPLEX_UNIT_SP, font);
            secondClock.setTextSize(TypedValue.COMPLEX_UNIT_SP, font);
        });
    }
}
