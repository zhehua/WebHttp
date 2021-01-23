package com.github.zhehua.webhttp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import org.bytedeco.javacpp.Loader;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_WRITE_EXTERNAL_STORAGE = 1;
    private final int FILE_SELECT_CODE = 1;//文件选择的代码
    private Button chooser, button, close;//点击按钮调用系统的选择器来选择文件
    private Uri uri = null;
    private ListView fileListView;
    private EditText msg;
    private FileAdapter fileAdapter;
    private FileServer fileServer;
    private ImageView imageView;
    private Bitmap bit;
    private static final int DEFAULT_SERVER_PORT = 8888;
    private int width = 800;
    private int height = 800;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPermission();
        url="http://"+getLocalIpStr(MainActivity.this) + ":" + DEFAULT_SERVER_PORT;
        button = findViewById(R.id.open);
        close = findViewById(R.id.close);
        msg = findViewById(R.id.msg);
        imageView = findViewById(R.id.imageView);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    pase();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // close();
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/hm/movies";
                File file = new File(path);
                File[] fs = file.listFiles();
                List<SharedFile> fileList = new ArrayList<>();
                for (File f : fs) {
                    SharedFile sharedFile = new SharedFile();
                    sharedFile.setName(f.getName());
                    sharedFile.setPath(f.getPath());
                    fileList.add(sharedFile);
                }
                if (fileServer != null) return;
                fileServer = new FileServer(fileList, DEFAULT_SERVER_PORT);
                try {
                    fileServer.start();

                    msg.setText(url);
                    zxing(url);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });


    }
    public  void pase() throws Exception {
        msg.setText("begin....");
        System.out.println("encode WEBM");
        String target = Environment.getExternalStorageDirectory().getAbsolutePath()+"\\dHZfB7161014.mp4";
        String source = "https://leshi.cdn-zuyida.com/20180205/iGSgTAHO/800kb/hls/dHZfB7161014.ts";//""C:\\Users\\pc\\Downloads\\dHZfB7161014.ts";

        String ffmpeg = Loader.load(org.bytedeco.ffmpeg.ffmpeg.class);
        ProcessBuilder pb = new ProcessBuilder(ffmpeg, "-i", source, "-vcodec", "h264", target);
        pb.start();//.waitFor();
        msg.setText("ok....");
    }

    //获取IP地址
    public static String getLocalIpStr(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        return intToIpAddr(wifiInfo.getIpAddress());
    }

    private static String intToIpAddr(int ip) {
        return (ip & 0xFF) + "."
                + ((ip >> 8) & 0xFF) + "."
                + ((ip >> 16) & 0xFF) + "."
                + ((ip >> 24) & 0xFF);
    }

    private void checkPermission() {
        //检查权限（NEED_PERMISSION）是否被授权 PackageManager.PERMISSION_GRANTED表示同意授权
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //用户已经拒绝过一次，再次弹出权限申请对话框需要给用户一个解释
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission
                    .WRITE_EXTERNAL_STORAGE)) {
                Toast.makeText(this, "请开通相关权限，否则无法正常使用本应用！", Toast.LENGTH_SHORT).show();
            }
            //申请权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_EXTERNAL_STORAGE);

        } else {
            // Toast.makeText(this, "授权成功！", Toast.LENGTH_SHORT).show();
            //Log.e(TAG_SERVICE, "checkPermission: 已经授权！");
        }
    }

    @Override
    protected void onPause() {
        System.out.println("ppppppppppppppppppppppppp");

        super.onPause();
    }

    @Override
    protected void onStop() {
        System.out.println("oooooooooooooooooooooooooooooooo");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        close();

        super.onDestroy();
    }

    void close() {
        if (fileServer != null) {
            fileServer.closeAllConnections();
            fileServer = null;
            Log.e("onPause", "app pause, so web server close");
        }
        msg.setText("");
        imageView.setVisibility(View.INVISIBLE);
    }

    private void zxing(String name) {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        Map<EncodeHintType, String> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8"); //记得要自定义长宽
        BitMatrix encode = null;
        try {
            encode = qrCodeWriter.encode(name, BarcodeFormat.QR_CODE, width, height, hints);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        int[] colors = new int[width * height];
        //利用for循环将要表示的信息写出来
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (encode.get(i, j)) {
                    colors[i * width + j] = Color.BLACK;
                } else {
                    colors[i * width + j] = Color.WHITE;
                }
            }
        }

        bit = Bitmap.createBitmap(colors, width, height, Bitmap.Config.RGB_565);
        imageView.setImageBitmap(bit);
        imageView.setVisibility(View.VISIBLE);
    }
}