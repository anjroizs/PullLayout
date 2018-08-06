package pull.js.com.pullwidget;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import widgets.PullLayout;

public class MainActivity extends AppCompatActivity implements PullLayout.OnPullListener {
    private PullLayout pullLayout;
    private ListView listView;
    private ArrayList data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pullLayout = findViewById(R.id.pullLayout);
        pullLayout.setOnPullListener(this);
        listView = findViewById(R.id.listView);
        data = new ArrayList();
        for (int i = 0; i < 25; i++) {
            data.add("Data" + i);
        }
        listView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, data));
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message message) {
            if (message.what == 0) {
                data = new ArrayList();
                for (int i = 0; i < 25; i++) {
                    data.add("Data" + i);
                }
                listView.setAdapter(new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, data));
            } else {
                int preLen = data.size();
                for (int i = preLen - 1; i < preLen + 25; i++) {
                    data.add("Data" + i);
                }
                ((ArrayAdapter) listView.getAdapter()).notifyDataSetChanged();
            }
            pullLayout.finished(PullLayout.SUCCESS);
            super.handleMessage(message);
        }

    };

    @Override
    public void onPullDown() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                handler.sendEmptyMessage(0);
            }
        }).start();
    }

    @Override
    public void onPullUp() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                handler.sendEmptyMessage(1);
            }
        }).start();

    }
}
