package pl.kelog;


import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ReverseAudioHandler implements RequestHandler<String, String> {
    
    private static final Logger log = Logger.getLogger(ReverseAudioHandler.class.getName());
    
    private static final String TEMP_INPUT_FILE = "/tmp/input.wav";
    private static final String TEMP_OUTPUT_FILE = "/tmp/output.wav";
    private static final List<String> FFMPEG_COMMAND = Arrays.asList("./ffmpeg", "-i", TEMP_INPUT_FILE, "-af", "areverse", TEMP_OUTPUT_FILE);
    
    @Override
    public String handleRequest(String inputBase64, Context context) {
        log.info("Request handler being called, input base64 size = " + inputBase64.length() + " bytes.");
        
        try {
            return process(inputBase64);
        } catch (Exception e) {
            log.log(Level.SEVERE, "Exception in handler", e);
            throw new RuntimeException(e);
        }
    }
    
    private String process(String inputBase64) throws Exception {
        byte[] sourceData = Base64.getDecoder().decode(inputBase64);
        
        log.info("Writing temporary file " + TEMP_INPUT_FILE + " size " + sourceData.length + " bytes.");
        FileUtils.writeByteArrayToFile(new File(TEMP_INPUT_FILE), sourceData);
        
        log.info("Launching ffmpeg, command line = " + FFMPEG_COMMAND + ".");
        Process proc = new ProcessBuilder(FFMPEG_COMMAND).start();
        proc.waitFor();
        log.info("Process returned exit status = " + proc.exitValue() + ".");
        
        log.info("Reading output file " + TEMP_OUTPUT_FILE + "...");
        byte[] bytes = FileUtils.readFileToByteArray(new File(TEMP_OUTPUT_FILE));
        log.info("Output file " + TEMP_OUTPUT_FILE + " size = " + bytes.length + " bytes.");
        
        return Base64.getEncoder().encodeToString(bytes);
    }
}
