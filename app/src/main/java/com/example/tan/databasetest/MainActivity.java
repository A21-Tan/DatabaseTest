package com.example.tan.databasetest;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;


public class MainActivity extends AppCompatActivity {
    final String DATABASE_NAME = "BookStore.db";
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final EditText nameET = findViewById(R.id.name);
        final EditText authorET = findViewById(R.id.author);
        final EditText pagesET = findViewById(R.id.pages);
        final EditText priceET = findViewById(R.id.price);

        /*创建数据库*/
        final MyDatabaseHelper helper = new MyDatabaseHelper(this, DATABASE_NAME,
                null, 2);
        Button createBtn = findViewById(R.id.create);
        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db = helper.getWritableDatabase();
            }
        });

        /*插入数据*/
        //AlertDialog.Builder builder = new AlertDialog.Builder(this);
        Button insertBtn = findViewById(R.id.insert);
        insertBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = nameET.getText().toString();
                String author = authorET.getText().toString();
                String pages = pagesET.getText().toString();
                String price = priceET.getText().toString();
                if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(name) &&
                        !TextUtils.isEmpty(name) && !TextUtils.isEmpty(name)) {
                    db.execSQL(String.format("insert into Book (name, author, pages, price) values(?, ?, ?, ?)"),
                            new String[]{name, author, pages, price});
                } else {
                    Toast.makeText(view.getContext(), "请输入完整的信息", Toast.LENGTH_SHORT).show();
                }
            }
        });

        /*更新数据*/
        Button updateBtn = findViewById(R.id.update);
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.execSQL("update Book set price = " + "'39.9'" + " where name = " + "'tan'");
            }
        });

        /*删除数据*/
        Button deleteBtn = findViewById(R.id.delete);
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.delete("Book", "price > ?", new String[]{"50"});
            }
        });

        /*查询数据*/
        Button selcetBtn = findViewById(R.id.select);
        selcetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Cursor cursor = db.rawQuery("select * from Book", null);
                if(cursor.moveToFirst()){
                    do{
                        String name = cursor.getString(cursor.getColumnIndex("name"));
                        String author = cursor.getString(cursor.getColumnIndex("author"));
                        String pages = cursor.getString(cursor.getColumnIndex("pages"));
                        String price = cursor.getString(cursor.getColumnIndex("price"));
                        Toast.makeText(view.getContext(), "name = "+name+"\nauthor = "+author+
                                       "\npages = "+pages+"\nprice = "+price, Toast.LENGTH_LONG).show();
                    }while (cursor.moveToNext());
                }
                cursor.close();
            }
        });
    }


    //但由于权限不足，无法拷贝
    private void copyDBToSDcrad() {
        String oldPath = "data/data/com.example.tan.databasetest/databases/" + DATABASE_NAME;
        String newPath = Environment.getExternalStorageDirectory() + File.separator + DATABASE_NAME;

        copyFile(oldPath, newPath);
    }

    /**
     * 复制单个文件
     *
     * @param oldPath String 原文件路径
     * @param newPath String 复制后路径
     * @return boolean
     */
    public void copyFile(String oldPath, String newPath) {
        int bytesum = 0;
        int byteread = 0;
        try {
            File oldFile = new File(oldPath);
            File newFile = new File(newPath);
            if (!newFile.exists()) {
                newFile.createNewFile();
            }
            if (oldFile.exists()) {
                FileInputStream in = openFileInput(oldPath);
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                FileOutputStream out = openFileOutput(newPath, Context.MODE_PRIVATE);
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));

                char[] buffer = new char[1444];
                while ((byteread = reader.read(buffer)) != -1) {
                    bytesum += byteread; // 字节数 文件大小
                    writer.write(buffer, 0, byteread);
                }

                reader.close();
                writer.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
