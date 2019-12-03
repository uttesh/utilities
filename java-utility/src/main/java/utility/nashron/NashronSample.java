package utility.nashron;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.script.Bindings;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class NashronSample {
	
	public static void main(String[] args) {
		ObjectMapper mapper = new ObjectMapper();
		EntityBean bean = new EntityBean();
		bean.setAddress("testing address");
		bean.setEmail("test@test.com");
		bean.setName("test entity");
		bean.setStatus("added");
		try {
			//FileInputStream fileInputStream = new FileInputStream(new File("migrate.js"));
			//Object bindingsResult = jsExecutor(fileInputStream, mapper.writeValueAsString(bean));
			//System.out.println("bindingsResult :: " + String.valueOf(bindingsResult));
			
			FileInputStream _fileInputStream = new FileInputStream(new File("migration.js"));
			Object bindingsResult = migrationExecutor(_fileInputStream, mapper.writeValueAsString(bean));
			System.out.println("migrationExecutor bindingsResult :: " + String.valueOf(bindingsResult));
			
		} catch (FileNotFoundException | JsonProcessingException e) {
			e.printStackTrace();
		}
	}

	private static Object jsExecutor(InputStream inputStream, Object data) {
		ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript");
		if (engine != null) {
			Invocable invocable = (Invocable) engine;
			Bindings bindings = engine.createBindings();
			bindings.put("entityData", data);
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
			try {
				StringBuffer stringBuffer = new StringBuffer();
				stringBuffer.append("function convert(entityData) { ");
				stringBuffer.append("\n");
				stringBuffer.append("var entityData = JSON.parse(entityData.entityData);");
				stringBuffer.append("\n");
				stringBuffer.append("var entity = {};");
				stringBuffer.append("\n");
				String line = "";
				while ((line = reader.readLine()) != null) {
					stringBuffer.append(line);
					stringBuffer.append("\n");
				}
				stringBuffer.append("\n");
				stringBuffer.append("return JSON.stringify(entity); \n }");
				System.err.println("stringBuffer.toString() :::: " + stringBuffer.toString());
				engine.eval(stringBuffer.toString());
				Object bindingsResult = invocable.invokeFunction("convert", bindings);
				return bindingsResult;
			} catch (ScriptException | NoSuchMethodException | IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	private static Object migrationExecutor(InputStream inputStream, Object data) {
		ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript");
		if (engine != null) {
			Invocable invocable = (Invocable) engine;
			try {
				FileInputStream fileInputStream = new FileInputStream(new File("migrate.js"));
				String migrationBody = getScriptData(fileInputStream);
				StringBuffer mograteScriptData = new StringBuffer();
				mograteScriptData.append("var entity = {}; \n");
				mograteScriptData.append(migrationBody+"\n");
				mograteScriptData.append("return entity;");
				Bindings bindings = engine.createBindings();
				bindings.put("entityData", data);
				bindings.put("migrationData", mograteScriptData.toString());
				engine.eval(getScriptData(inputStream));
				Object bindingsResult = invocable.invokeFunction("migrate", bindings);
				return bindingsResult;
			} catch (ScriptException | NoSuchMethodException | IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	private static String getScriptData(InputStream inputStream) {
		StringBuffer stringBuffer = new StringBuffer();
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
			String line = "";
			while ((line = reader.readLine()) != null) {
				stringBuffer.append(line);
				stringBuffer.append("\n");
			}
			stringBuffer.append("\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return stringBuffer.toString();
	}
}
