package com.abuhrov;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
import org.apache.commons.io.FileUtils;
import org.cloudinary.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Database {
    private static volatile Database instance;
    private static Cloudinary cloudinary = new Cloudinary("cloudinary://746638216764126:XmFQQYgkpSqobGUVCVFn-nGs2lI@bugrovtolik");

    public static Database getInstance() {
        Database localInstance = instance;
        if (localInstance == null) {
            synchronized (Database.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new Database();
                }
            }
        }

        return localInstance;
    }

    public void save(JSONObject db) {
        try {
            cloudinary.uploader().upload(db, ObjectUtils.asMap(
                    "public_id", "db.json"
            ));
        } catch (IOException e) {
            System.out.println(e.toString());
        }
    }

    public JSONObject get() {
        JSONObject db;
        try {
            FileUtils.copyURLToFile(new URL("https://res.cloudinary.com/bugrovtolik/raw/upload/db.json"),
                    new File("db.json"));

            db = new JSONObject(Files.readString(Path.of("db.json")));
        } catch (IOException e) {
            db = new JSONObject();
        }

        return db;
    }
}
