package com.example.sma.readfile;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Xml;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Scanner;

public class MainActivity extends Activity {

    TextView textFile;
    Resources res;
    ArrayList<String> dirList = new ArrayList<>();
    ArrayAdapter<String> adapter;
    ListView listDir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textFile = (TextView) findViewById(R.id.textBook);
        listDir = (ListView) findViewById(R.id.listDir);
        res = getResources();
       // readFile();
        readDirectory(Environment.getExternalStorageDirectory().getPath());
        adapter = new ArrayAdapter<String>(this,R.layout.item_list, dirList);
        listDir.setAdapter(adapter);
        listDir.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String fileName = dirList.get(position);
                File file = new File(fileName);
                if(file.isDirectory()) {
                    dirList.clear();
                    readDirectory(fileName);
                    adapter.addAll(dirList);
                }
                else {
                    //readFile(fileName);
                    //readFileFromRes();
                    parseXMLFileFromRes();
                }
            }
        });
    }

    private void  readDirectory(String path) {
        //получаем список файлов и директорий внутри текущей директории
        File[] fileList = new File(path).listFiles();
        for (File file : fileList) {
                //сохраняем имена файлов и директорий в список имен
                dirList.add(file.getAbsolutePath());
        }
    }
    private void readFile(String path)  {
        File file = new File(path);
        BufferedReader br;
        String str = "";
        str=file.getName();
        if(file.canRead() && (str.endsWith("fb2")||str.endsWith("txt"))) {
            if(str.endsWith("fb2"))
                parseXMLFile(path);
            else
                try {
                    FileReader fl = new FileReader(path);
                    br = new BufferedReader(fl);
                    while ((str = br.readLine()) != null) {
                        textFile.append(str + "\n");
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Toast.makeText(this,"Файл не найден",Toast.LENGTH_LONG).show();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        else Toast.makeText(this,"Файл недоступен для чтения или имеет неправильный формат",Toast.LENGTH_LONG).show();
    }

    private void parseXMLFile(String path){
        XmlPullParser parser = Xml.newPullParser();
        String s = "";
        try {
            FileReader fl = new FileReader(path);
            parser.setInput(fl);
            while (parser.getEventType()!= XmlPullParser.END_DOCUMENT){
                if(parser.getEventType() == XmlPullParser.START_TAG)
                    s = parser.getName();
                parser.nextText();
                if (s.equals("p")&& parser.getEventType()== XmlPullParser.TEXT) {
                    String text = parser.getText();
                    textFile.append(text + "\n");
                }
                if (parser.getEventType() == XmlPullParser.START_TAG &&
                        (parser.getName().equals("section") || parser.getName().equals("subtitle")))
                    textFile.append("\n\n");
                parser.next();

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(this,"Файл не найден",Toast.LENGTH_LONG).show();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
    }

    private void readFileFromRes()  {
        BufferedReader br;
        String str = "";
        FileInputStream stream = null;
            try {
                 br = new BufferedReader(new InputStreamReader(res.openRawResource(R.raw.belyanin1), "windows-1251"));
                while ((str = br.readLine()) != null) {
                    textFile.append(str + "\n");
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(this,"Файл не найден",Toast.LENGTH_LONG).show();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    private void parseXMLFileFromRes(){
        XmlPullParser parser = Xml.newPullParser();
        String s = "";
        try {
            InputStream stream = getApplicationContext().getAssets().open("under.fb2");
            parser.setInput(stream,"utf-8");
            while (parser.getEventType()!= XmlPullParser.END_DOCUMENT){
                if(parser.getEventType() == XmlPullParser.START_TAG)
                    s = parser.getName();
                if (s.equals("p")&& parser.getEventType()== XmlPullParser.TEXT) {
                    String text = parser.getText();
                    textFile.append(text + "\n");
                }
                if (parser.getEventType() == XmlPullParser.START_TAG &&
                        (parser.getName().equals("section") || parser.getName().equals("subtitle")))
                    textFile.append("\n\n");
                parser.next();

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(this,"Файл не найден",Toast.LENGTH_LONG).show();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
