package vn.yinx.autodeploy.controllers;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Random;

import org.json.simple.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import vn.yinx.autodeploy.models.IdCard;
import vn.yinx.autodeploy.util.HttpUtils;

@Controller
@RequestMapping("/api/v1/ocr")
public class ocr {
	private Random rd = new Random();

	@PostMapping("")
	public ResponseEntity<JSONObject> getAll(@RequestBody IdCard idCard) {
		// Save
		JSONObject res = new JSONObject();// value return
		res.put("status", 1);// default: success

		try {
			byte[] data = Base64.getDecoder().decode(idCard.getImage().getBytes(StandardCharsets.UTF_8));
			File fo = new File("images/" + System.currentTimeMillis() + "." + rd.nextLong() + ".jpg");
			if (!fo.getParentFile().isDirectory()) {
				fo.getParentFile().mkdirs();
			}
			FileOutputStream fos = new FileOutputStream(fo);
			fos.write(data);
			fos.close();
			long start = System.currentTimeMillis();
			JSONObject rs = HttpUtils.sendFile("https://api.fpt.ai/vision/idr/vnm", "hmHH4mRRoYJZkIimpulm8pQ8X0uV1N2s",
					fo);
			System.out.println("Time request: " + (System.currentTimeMillis() - start));
			System.out.println(rs);
			if(rs.get("message").toString().toLowerCase().equals("api rate limit exceeded.")) {
				//Change key
			}
			res.put("data", rs.get("data"));// put data by key "data"
			return ResponseEntity.ok(res);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			res.put("status", 0);
			res.put("message", e.getMessage());
			return ResponseEntity.ok((JSONObject) res.get("data"));
		}
//		JSONObject res = new JSONObject();
//		res.put("name", "Yin");
//		res.put("birth", "15/06/1996");
//		res.put("birth-place", "Thai Binh");
//		return ResponseEntity.ok(res);
	}
}
