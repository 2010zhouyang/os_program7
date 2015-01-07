import java.io.*;
import java.nio.*;
import java.nio.file.*;
import java.nio.channels.*;
import java.util.*;
import java.util.concurrent.Future;
import java.nio.charset.Charset;
import java.nio.channels.AsynchronousFileChannel;
import java.util.logging.*;

import java.lang.System;
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
// Obtain a logger
private static Logger logger = Logger.getLogger(NIOStarter.class.getName());
private static FileHandler fh ;


public static void main(String[] args) throws Exception {
if (args.length != 1) {
System.out.println("Usage: NIOStarter <inputfile>");
System.exit(0);
}

try{
	fh = new FileHandler("aio.log");
	logger.addHandler(fh);
	SimpleFormatter formatter = new SimpleFormatter();
	fh.setFormatter(formatter);
	logger.info("Log File: Exception Handling:");
}catch(SecurityException e){
	e.printStackTrace();
}

try {
File inputFile = new File(args[0]);
FileInputStream fstream = new FileInputStream(inputFile);
DataInputStream in = new DataInputStream(fstream);
BufferedReader br = new BufferedReader(new InputStreamReader(in));
String inputLine;
while ((inputLine = br.readLine()) != null) {
String[] tokens = inputLine.split(" ");

// Check tokens quantity
if(tokens.length !=4){
logger.log(Level.SEVERE, "Parsing input file failure");
System.exit(1);
}

// Check input file format and check if read files exist
if(checkInput(tokens[0]) && checkFileExist(tokens[1]) && checkInput(tokens[2]) && checkInput(tokens[3])) {
FileSystem fs = FileSystems.getDefault();
//System.out.println(fs.getPath(tokens[1]).toString());
FileRecord record = new FileRecord(Integer.parseInt(tokens[0]), fs.getPath(tokens[1]), Integer.parseInt(tokens[2]), Integer.parseInt(tokens[3]));
readData(record);
}
else{
// input format error
logger.log(Level.SEVERE, "Input file format failure Or read files not exist");
System.exit(1);
}

}
in.close();
}
catch(IOException e) {
	// Logger put here
	// System.out.println("============Log Info:: Severe Error===================");
	logger.log(Level.SEVERE, "I/O Exceptions");
	// logger.log(Level.SEVERE, "I/O Exceptions File Doesnot exist", e);
	//e.printStackTrace(System.out);
	// Terminate program
	System.exit(1);
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
File file = new File(path);
if(file.exists() && !file.isDirectory())
	return true;
else
	return false;
}
private static void readData(FileRecord record) throws Exception{
//System.out.println(record.getOpid());
//System.out.println(record.getInputFilePath().toString());
//System.out.println(record.getByteOffset());
//System.out.println(record.getByteCount());
//String filePath = record.getInputFilePath().toString();

// Check invalid reads
File f = new File(record.getInputFilePath().toString());
long fileSize = f.length();
if(record.getByteOffset()<0 || record.getByteCount() <=0 || (record.getByteOffset()+record.getByteCount())> fileSize  ){
	logger.log(Level.WARNING, "Invalid reads");
}

else{
	ByteBuffer bbuff = ByteBuffer.allocate(record.getByteCount());
	String encoding = System.getProperty("file.encoding");
	//System.out.println("The File Location:"+filePath);
	AsynchronousFileChannel channel = null;
	try {
	channel = AsynchronousFileChannel.open(record.getInputFilePath(), StandardOpenOption.READ);
	//System.out.println("File Size: " + channel.size());
	Future<Integer> result = channel.read(bbuff,record.getByteOffset());
	while(!result.isDone()) {
	//System.out.println("So elese:");
	}
	//System.out.println("Read Done: " + result.isDone());
	//System.out.println("Bytes read: " + result.get());
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
	System.out.println(record.getOpid()+ " " +Charset.forName(encoding).decode(bbuff));
	bbuff.clear();
	}
}
}
