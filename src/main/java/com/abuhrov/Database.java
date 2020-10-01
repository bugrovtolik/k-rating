package com.abuhrov;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.apache.commons.io.FileUtils;
import org.cloudinary.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

public class Database {
	private static Database instance;
    private static final Cloudinary cloudinary =
            new Cloudinary("cloudinary://746638216764126:XmFQQYgkpSqobGUVCVFn-nGs2lI@bugrovtolik");

	public synchronized static Database getInstance() {
		if (instance == null) {
			instance = new Database();
        }

		return instance;
    }

    public void save(JSONObject db) {
        try {
            Files.writeString(Path.of("db.json"), db.toString());
            cloudinary.uploader().destroy("db.json", ObjectUtils.asMap("invalidate", "true", "resource_type", "raw"));
            cloudinary.uploader().upload(new File("db.json"), ObjectUtils.asMap("public_id", "db.json",
                    "resource_type", "raw"));
        } catch (Exception e) {
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
