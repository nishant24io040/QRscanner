package com.example.qrscanner;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.client.android.Intents;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    ImageView button;
    String scr;
    Button logoutbtn;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        window.setStatusBarColor(ContextCompat.getColor(this,R.color.black));
        button = findViewById(R.id.button);
        logoutbtn = findViewById(R.id.sout);
        final ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(new ScanContract(),
                result -> {
                    if(result.getContents() == null) {
                        Toast.makeText(MainActivity.this, "Cancelled", Toast.LENGTH_LONG).show();
                    } else {
                        scr = result.getContents();
                        Map<String,String> item = new HashMap<>();
                        item.put("QR decoded data ",scr);
                        db.collection(mAuth.getCurrentUser().getDisplayName()).add(item)
                                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentReference> task) {
                                        Toast.makeText(MainActivity.this, "QR Code Data Uploaded on FireStore DB ", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                });
        button.setOnClickListener(v -> {
            ScanOptions intentIntegrator = new ScanOptions();
            intentIntegrator.setPrompt("scan now");
            intentIntegrator.setOrientationLocked(false);
            intentIntegrator.setDesiredBarcodeFormats(ScanOptions.QR_CODE);
            barcodeLauncher.launch(intentIntegrator);
        });
        logoutbtn.setOnClickListener(v -> {
            mAuth.signOut();
            Intent intent = new Intent(this,GoogleLogin.class);
            startActivity(intent);
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result!=null){
            String contains = result.getContents();
            if (contains!=null){
                Toast.makeText(this, contains, Toast.LENGTH_SHORT).show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}