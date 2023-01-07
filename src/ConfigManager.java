

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;
import java.util.StringTokenizer;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class ConfigManager {
	
	HashMap <String, JSONObject> jsonFiles;
	
	public ConfigManager() {
		jsonFiles = new HashMap <String, JSONObject>();
	}
	
	public void read_files() {
		try {
			File folder = new File("config");
			File[] listOfFiles = folder.listFiles();
			int count = 0;
			for (int i = 0; i < listOfFiles.length; i++) {
			  if (listOfFiles[i].isFile()) {
				String name = listOfFiles[i].getName();
				JSONObject config_file = (JSONObject)new JSONParser().parse(new FileReader("config//"+name));
				jsonFiles.put(name.substring(0, name.length() - 5), config_file);
			    count +=1;
			  }
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public JSONObject get_file(String name) {
		return jsonFiles.get(name);
	}
	
	public HashMap <Object, Object> get_object (String filename, String directory) {
		StringTokenizer tokens = new StringTokenizer(directory, "/");
		JSONObject json_file = jsonFiles.get(filename);
		JSONObject current_key = json_file;
		while (tokens.hasMoreTokens()) {
			String dir = tokens.nextToken();
			current_key = (JSONObject) current_key.get(dir);
		}
		return  current_key;
	}
	
//	public Object get_value(String filename, String directory) {
//		StringTokenizer tokens = new StringTokenizer(directory, "/");
//		JSONObject json_file = jsonFiles.get(filename);
//		JSONObject current_key = json_file;
//		while (tokens.hasMoreTokens()) {
//			String dir = tokens.nextToken();
//			current_key = (JSONObject) current_key.get(dir);
//		}
//		return  (Object)current_key;
//	}
	
	public String[] get_keys(String filename, String directory) {
		StringTokenizer tokens = new StringTokenizer(directory, "/");
		JSONObject json_file = jsonFiles.get(filename);
		JSONObject current_key = json_file;
		while (tokens.hasMoreTokens()) {
			String dir = tokens.nextToken();
			current_key = (JSONObject) current_key.get(dir);
		}
		Set<String> key_set = current_key.keySet();
		String keys[] = new String[key_set.size()];
		int i = 0;
		for (String key: key_set) {
			keys[i] = key;
			i +=1;
		}
		
		for (i = 0; i < keys.length; i++) {
			System.out.println("get keys: "+keys[i]);
		}
		return keys;
	}
}
