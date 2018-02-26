import com.jcraft.jsch.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class will allow you to execute any command on a foreign computer
 * through SSH.
 */
public class SSHHandler {

    public void execCommand(String userName, String ip, int port, String password, String command){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    JSch.setConfig("StrictHostKeyChecking", "no");
                    JSch jsch = new JSch();
                    Session session = jsch.getSession(userName, ip, port);
                    session.setPassword(password);
                    session.connect();

                    Channel channel = session.openChannel("shell");
                    channel.connect();

                    OutputStream output = channel.getOutputStream();
                    PrintStream printStream = new PrintStream(output, true);
                    InputStream commandOutput = channel.getInputStream();

                    printStream.println(command);

                    byte[] temp = new byte[1024];
                    while(true){
                        while(commandOutput.available() > 0){
                            int readByte = commandOutput.read(temp, 0, 1024);
                            if(readByte < 0){
                                break;
                            }
                            System.out.println(new String(temp, 0, readByte));
                        }
                        if(channel.isClosed()){
                            if(commandOutput.available() > 0){
                                continue;
                            }
                            System.out.println("exit-status: " + channel.getExitStatus());
                            break;
                        }
                    }
                    channel.disconnect();
                    session.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }
}