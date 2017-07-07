package per.wjw.edi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

public class ParseEddb {

	public static void main(String[] args) {
		HttpClient client = HttpClients.createDefault();
		HttpGet request = new HttpGet("https://eddb.io/archive/v5/systems_populated.json");
		try {
			HttpEntity entity = client.execute(request).getEntity();
			StringBuffer buff = new StringBuffer();
			String line = null;
			BufferedReader reader = new BufferedReader(new InputStreamReader(entity.getContent()));
			while( (line=reader.readLine())!= null){
				buff.append(line);
			}
			int rhe=3;
			EntityUtils.consume(entity);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
