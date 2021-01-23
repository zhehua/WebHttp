//package com.github.zhehua.webhttp;
//
//import android.content.Intent;
//import android.net.Uri;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.Button;
//import android.widget.ListView;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import java.util.Date;
//
////
//public class FileChooser extends AppCompatActivity {
//    private final int FILE_SELECT_CODE = 1;//文件选择的代码
//    private Button chooser;//点击按钮调用系统的选择器来选择文件
//    private Uri uri = null;
//    private ListView fileListView;
//    private FileAdapter fileAdapter;
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.filechooser);
//        initView();
//        initListener();
//    }
//
//    private void initView() {
//        chooser = findViewById(R.id.chooser);
//        fileListView = findViewById(R.id.filelist);
//        fileAdapter = new FileAdapter(Status.fileLists, this);
//        fileListView.setAdapter(fileAdapter);
//    }
//
//    private void initListener() {
//        chooser.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                showFileChooser();
//            }
//        });
//    }
//    //文件选择
//    private void showFileChooser() {
//        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//        intent.setType("*/*");//任意文件都可以分享
//        intent.addCategory(Intent.CATEGORY_OPENABLE);
//        //调用系统的文件选择器
//        startActivityForResult(Intent.createChooser(intent, "请选择分享的文件"), FILE_SELECT_CODE);
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        switch (requestCode) {
//            case FILE_SELECT_CODE:
//                if (resultCode == RESULT_OK) {
//                    uri = data.getData();
//                    SharedFile sharedFile = new SharedFile();
//                    sharedFile.setPath(FileUtils.getPath(this,uri));//这个地方就是坑了，直接选择文件的路径在7.0的手机和7.1的手机上面都是不对的
//                    sharedFile.setName(path2Name(FileUtils.getPath(this,uri)));
//                    sharedFile.setSharedtime(getTime());
//                    Status.fileLists.add(sharedFile); //分享文件列表是全局的，所以文件选择器可以向其中添加文件，服务器也可以从其中读取文件信息。
//                    fileAdapter.notifyDataSetChanged();
//                }
//                break;
//        }
//        super.onActivityResult(requestCode, resultCode, data);
//    }
//
//    //将文件完整路径转化为文件名
//    private String path2Name(String path){
//        String name=null;
//        int len=path.length();
//        if(path.charAt(len-1)=='/'){
//            int a=-1,b=0;
//            while((a=path.indexOf('/',b+1))>0){
//                if(a==len-1)break;
//                b=a;
//            }
//            name = path.substring(b+1,len);
//        }else {
//            int a=-1,b=0;
//            while((a=path.indexOf('/',b+1))>0){
//                if(a==len-1)break;
//                b=a;
//            }
//            name = path.substring(b+1,len);
//        }
//        return name;
//    }
//    //分享文件的时间
//    private String getTime(){
//        Date date = new Date();
//        return date.toString();
//    }
//}