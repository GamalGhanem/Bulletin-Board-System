import com.jcraft.jsch.*;

import java.io.IOException;
import java.io.InputStream;
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
                    JSch jsch = new JSch();
                    Session session = jsch.getSession(userName, ip, port);
                    session.setPassword(password);
                    session.setConfig("StrictHostKeyChecking", "no");
                    session.setX11Host(ip);
                    session.setX11Port(6000);
                    session.connect();

                    ChannelExec channelExec = (ChannelExec) session.openChannel("exec");
                    channelExec.setCommand(command);
                    channelExec.setErrStream(System.err);
                    channelExec.connect();
                    InputStream commandOutput = channelExec.getInputStream();

                    byte[] temp = new byte[1024];
                    while(true){
                        while(commandOutput.available() > 0){
                            int readByte = commandOutput.read(temp, 0, 1024);
                            if(readByte < 0){
                                break;
                            }
                            System.out.println(new String(temp, 0, readByte));
                        }
                        if(channelExec.isClosed()){
                            if(commandOutput.available() > 0){
                                continue;
                            }
                            System.out.println("exit-status: " + channelExec.getExitStatus());
                            break;
                        }
                    }
                    channelExec.disconnect();
                } catch (IOException | JSchException e){
                    e.printStackTrace();
                }
            }
        }).start();

    }
}
