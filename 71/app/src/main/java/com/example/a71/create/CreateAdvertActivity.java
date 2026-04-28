package com.example.a71.create;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.example.a71.R;
import com.example.a71.database.AppDatabase;
import com.example.a71.database.ItemEntity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class CreateAdvertActivity extends AppCompatActivity {

    EditText etName, etPhone, etDescription, etCategory, etLocation;

    RadioGroup radioGroup;
    Button btnSave, btnSelectImage;

    TextView btnSelectDate, tvImageStatus;

    Uri imageUri;
    AppDatabase db;

    Calendar selectedCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_advert);

        db = AppDatabase.getInstance(this);

        // Bind views
        etName = findViewById(R.id.etName);
        etPhone = findViewById(R.id.etPhone);
        etDescription = findViewById(R.id.etDescription);
        etCategory = findViewById(R.id.etCategory);
        etLocation = findViewById(R.id.etLocation);

        radioGroup = findViewById(R.id.radioGroup);

        btnSave = findViewById(R.id.btnSave);
        btnSelectImage = findViewById(R.id.btnSelectImage);

        btnSelectDate = findViewById(R.id.btnSelectDate);
        tvImageStatus = findViewById(R.id.tvImageStatus);

        // IMAGE PICKER
        btnSelectImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.setType("image/*");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
            startActivityForResult(intent, 1);
        });

        // DATE PICKER (defaults to today)
        btnSelectDate.setOnClickListener(v -> {

            Calendar calendar = Calendar.getInstance();

            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog picker = new DatePickerDialog(this,
                    (view, y, m, d) -> {

                        selectedCalendar.set(y, m, d);

                        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
                        btnSelectDate.setText(sdf.format(selectedCalendar.getTime()));

                    },
                    year, month, day);

            picker.show();
        });

        // SAVE
        btnSave.setOnClickListener(v -> {

            if (imageUri == null) {
                Toast.makeText(this, "Select an image", Toast.LENGTH_SHORT).show();
                return;
            }

            if (selectedCalendar == null) {
                Toast.makeText(this, "Select a date", Toast.LENGTH_SHORT).show();
                return;
            }

            int selectedId = radioGroup.getCheckedRadioButtonId();
            if (selectedId == -1) {
                Toast.makeText(this, "Select Lost or Found", Toast.LENGTH_SHORT).show();
                return;
            }

            String type = (selectedId == R.id.radioLost) ? "Lost" : "Found";

            ItemEntity item = new ItemEntity();
            item.type = type;
            item.name = etName.getText().toString();
            item.phone = etPhone.getText().toString();
            item.description = etDescription.getText().toString();
            item.category = etCategory.getText().toString();
            item.location = etLocation.getText().toString();

            item.timestamp = System.currentTimeMillis();
            item.lostFoundTimestamp = selectedCalendar.getTimeInMillis();
            item.imageUri = imageUri.toString();

            new Thread(() -> {
                db.itemDao().insert(item);

                runOnUiThread(() -> {
                    Toast.makeText(this, "Saved!", Toast.LENGTH_SHORT).show();
                    finish();
                });

            }).start();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {

            imageUri = data.getData();

            if (imageUri != null) {

                final int takeFlags =
                        Intent.FLAG_GRANT_READ_URI_PERMISSION;

                getContentResolver().takePersistableUriPermission(
                        imageUri,
                        takeFlags
                );

                tvImageStatus.setText("Image selected ✓");
            }
        }
    }
}