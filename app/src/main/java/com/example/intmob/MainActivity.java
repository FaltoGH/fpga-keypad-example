package com.example.intmob;

import static java.lang.Thread.*;
import androidx.annotation.*;
import androidx.appcompat.app.*;
import android.os.*;
import android.widget.*;
import com.example.intmob.databinding.*;
import java.io.*;
import java.lang.Process;
import java.nio.file.*;
import java.nio.file.attribute.*;
import java.nio.file.*;
import java.nio.file.attribute.*;
import java.util.*;

public class MainActivity extends AppCompatActivity {

    // Used to load the 'intmob' library on application startup.
    static {
        System.loadLibrary("intmob");
    }

    private ActivityMainBinding binding;
    private MyThread m_thread;
    private SetTextHandler setTextHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        setTextHandler = new SetTextHandler();

        m_thread = new MyThread();
        m_thread.start();
    }

    private void setText(Object text){
        // text must be set by event handler
        // otherwise application crashes!
        Message msg1 = setTextHandler.obtainMessage(0, text);
        setTextHandler.sendMessage(msg1);
    }

    private class MyThread extends Thread
    {
        public void run(){
            try{
                Process p = Runtime.getRuntime().exec("su");
                DataOutputStream os = new DataOutputStream(p.getOutputStream());
                os.writeBytes("chmod 777 /dev/input/*\n");
                os.writeBytes("exit\n");
                os.flush();
                os.close();
                try {
                    p.waitFor();
                } catch (InterruptedException e) {
                    setText("waitFor:" + e.toString());
                }



                // event file name can be "event0", "event1", ...
                // event file name can change whenever device reboots.
                final String event_file_name = idev();



                while(true){
                    Thread.sleep(1);
                    String str1 = stringFromJNI(event_file_name);
                    System.out.println("keypad pressed: " + str1);
                    setText(str1);
                }
            }
            catch(Exception e){
                setText("run:"+e.toString());
            }
        }
    }

    private class SetTextHandler extends Handler
    {
        public void handleMessage(Message msg ){
            TextView v = binding.sampleText;
            Object obj = msg.obj;
            String sobj = obj.toString();
            v.setText(sobj);
        }
    }

    /**
     * A native method that is implemented by the 'intmob' native library,
     * which is packaged with this application.
     */
    private native String stringFromJNI(String event);
    private native void div0();
    private native String idev();
}