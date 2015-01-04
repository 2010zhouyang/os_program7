import java.io.*;
import java.nio.*;
import java.nio.file.*;
import java.nio.channels.*;
import java.util.*;
import java.util.concurrent.Future;
import java.nio.charset.Charset;
import java.nio.channels.AsynchronousFileChannel;
class FileRecord {
	int opid;
	Path inputFilePath;
	int byteOffset;
	int byteCount;
	
	public FileRecord(int opid, Path inputFilePath, int byteOffset, int byteCount) {
		this.opid = opid;
		this.inputFilePath = inputFilePath;
		this.byteOffset = byteOffset;
		this.byteCount = byteCount;
	}
	
	public int getOpid() {
		return this.opid;
		}
	public Path getInputFilePath() {
		return this.inputFilePath;
		}
	public int getByteOffset() {
		return this.byteOffset;
		}
	public int getByteCount() {
		return this.byteCount;
		}
}

public class NIOStarter {
	public static void main(String[] args) throws Exception {
		if (args.length != 1) {
			System.out.println("Usage: NIOStarter <inputfile>");
			System.exit(0);
		}
			
		try {
		File inputFile = new File(args[0]);
		FileInputStream fstream = new FileInputStream(inputFile);
		DataInputStream in = new DataInputStream(fstream);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String inputLine;
	while ((inputLine = br.readLine()) != null) {
	    String[] tokens = inputLine.split(" ");
	    if(checkInput(tokens[0]) && checkFileExist(tokens[1]) && checkInput(tokens[2]) && checkInput(tokens[3])) {
		FileSystem fs = FileSystems.getDefault();
		//System.out.println(fs.getPath(tokens[1]).toString());
		FileRecord record = new FileRecord(Integer.parseInt(tokens[0]), fs.getPath(tokens[1]), Integer.parseInt(tokens[2]), Integer.parseInt(tokens[3]));	
		readData(record);
		}
		//for(String s: tokens) {
		//	System.out.println(s);
		//	}
	}
	in.close();
	}
	catch(IOException e) {
	// Logger put here
	e.printStackTrace(System.out);	
	}
}

private static boolean checkInput(String text) {
	try {
	    Integer.parseInt(text);
	    return true;
	}
	catch (NumberFormatException e) {
	    return false;
	}

}

private static boolean checkFileExist(String path) {
return true;
}

private static void readData(FileRecord record) throws Exception{
	//System.out.println(record.getOpid());
	//System.out.println(record.getInputFilePath().toString());
	//System.out.println(record.getByteOffset());
	//System.out.println(record.getByteCount());
	//String filePath = record.getInputFilePath().toString();
	ByteBuffer bbuff = ByteBuffer.allocate(1024); 
	String encoding = System.getProperty("file.encoding");

	//System.out.println("The File Location:"+filePath);
	AsynchronousFileChannel channel = null;
	try {
	    channel = AsynchronousFileChannel.open(record.getInputFilePath(), StandardOpenOption.READ);
	    System.out.println("File Size: " + channel.size());
	    Future<Integer> result = channel.read(bbuff,0);
	
	    while(!result.isDone()) {
			//System.out.println("So elese:");	
		}
	    System.out.println("Read Done: " + result.isDone());
	    System.out.println("Bytes read: " + result.get());	
	    	
	}
	catch (Exception e) {
		System.out.println("Exception:");
		}

	finally {
		if(channel != null) {
			channel.close();
		}
	
	}
	
	bbuff.flip();
	System.out.println(Charset.forName(encoding).decode(bbuff));
	bbuff.clear();	
}
}

