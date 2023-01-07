

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class ResourceManager {

	HashMap<String,String> resources[];
	HashMap<String,Integer> resource_type;
	
	public ResourceManager(ConfigManager ConfigManage)
	{
		JSONObject resource_file = ConfigManage.get_file("resource");
		int length = resource_file.size();
		resource_type = new HashMap<String,Integer>();
		resources = new HashMap[length];
		Set<String> names = resource_file.keySet();
		int i=0;
		for(String name:names)
		{
			System.out.println("checking: "+name);
			resources[i] = new HashMap<String,String>();
			resource_type.put(name, i);
			JSONObject resource_obj = (JSONObject)resource_file.get(name);
			Set <String> resource_names = resource_obj.keySet();
			for(String resource_name:resource_names)
			{
				resources[i].put(resource_name, (String)resource_obj.get(resource_name));
			}
			i++;
		}
	}
	
	public String get_resource_folder(String type)
	{
		return resources[resource_type.get(type)].get("folder");
	}
	
	public String get_resource_dir(String type,String resource_name)
	{
		return  resources[resource_type.get(type)].get("folder")+"//"+resources[resource_type.get(type)].get(resource_name);
	}
	
	public String [][] get_all_resource_dir(String type)
	{
		int type_val = resource_type.get(type);
		int length=resources[type_val].size() - 1;
		String [][] dirs = new String[length][2];
		int i=0;
		for(String res:resources[type_val].keySet())
		{
			if (res.equals("folder"))
				continue;
			dirs[i][0] = res;
			dirs[i][1] = resources[type_val].get(res);
			i++;
		}
		return dirs;
	}
	
	public void add_resource(String res_type, String res_key, String res_val) {
		int id = resource_type.get(res_type);
		resources[id].put(res_key, res_val);
	}
	
	public void write_resource_file() {
		Set <String> res_type_set = resource_type.keySet();
		JSONObject resource_file = new JSONObject();
		for(String res_type : res_type_set) {
			JSONObject resource_object =new JSONObject();
			int id = resource_type.get(res_type);
			Set <String> res_key_set = resources[id].keySet();
			for(String res_key: res_key_set) {
				String res_val = resources[id].get(res_key);
				resource_object.put(res_key,res_val);
				if(res_type.equals("animation") && res_key.equals("folder")) {
					System.out.println(" checking: "+resource_object.toString().replace("\\/", "/"));
				}
			}
			resource_file.put(res_type, resource_object);
		}
        PrintWriter pw;
		try {
			pw = new PrintWriter("config/resource.json");
	        pw.write(resource_file.toJSONString().replace("\\/", "/")); 
	        pw.flush(); 
	        pw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} 
	}
}
