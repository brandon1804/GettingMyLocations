package sg.edu.rp.id18044455.gettingmylocations;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

public class RecordsActivity extends AppCompatActivity {

    TextView tvRecords;
    Button btnRefresh;
    ListView lvRecords;
    ArrayAdapter aa;
    ArrayList<String> records;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_records);

        tvRecords = findViewById(R.id.tvRecords);
        btnRefresh = findViewById(R.id.btnRefresh);
        lvRecords = findViewById(R.id.lvRecords);
        records = new ArrayList<>();

        aa = new ArrayAdapter(RecordsActivity.this, android.R.layout.simple_list_item_1, records);
        lvRecords.setAdapter(aa);

        String folderLocation = Environment.getExternalStorageDirectory().getAbsolutePath() + "/PSFolder";
        File targetFile = new File(folderLocation, "records.txt");
        if (targetFile.exists() == true) {
            records.clear();
            try {
                FileReader reader = new FileReader(targetFile);
                BufferedReader br = new BufferedReader(reader);
                String line = br.readLine();
                while (line != null) {
                    records.add(line);
                    line = br.readLine();
                }
                br.close();
                reader.close();
                aa.notifyDataSetChanged();
                tvRecords.setText("Number of records: " + records.size());
            } catch (Exception e) {
                Toast.makeText(RecordsActivity.this, "Failed to read!", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }


        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String folderLocation = Environment.getExternalStorageDirectory().getAbsolutePath() + "/PSFolder";
                File targetFile = new File(folderLocation, "records.txt");
                if (targetFile.exists() == true) {
                    records.clear();
                    try {
                        FileReader reader = new FileReader(targetFile);
                        BufferedReader br = new BufferedReader(reader);
                        String line = br.readLine();
                        while (line != null) {
                            records.add(line);
                            line = br.readLine();
                        }
                        br.close();
                        reader.close();
                        aa.notifyDataSetChanged();
                        tvRecords.setText("Number of records: " + records.size());
                    } catch (Exception e) {
                        Toast.makeText(RecordsActivity.this, "Failed to read!", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                }

            }

        });




    }//end of onCreate

}//end of class