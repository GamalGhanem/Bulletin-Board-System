import com.jcraft.jsch.*;

import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class will allow you to execute any command on a foreign computer
 * through SSH.
 */
public class SSHHandler {

    private static final Logger LOG = Logger.getLogger(SSHHandler.class.getName());

    private JSch jschChannel;
    private String userName, password, connectionIp;
    private int connectionPort;
    private int timeOut;
    private Session connectionSession;

    public SSHHandler(String userName, String password, String connectionIp, String knownHostsFileName){
        initActions(userName, password, connectionIp, knownHostsFileName);
        this.connectionPort = 22;
        this.timeOut = 60000;
    }

    public SSHHandler(String userName, String password, String connectionIp, String knownHostsFileName, int connectionPort){
        initActions(userName, password, connectionIp, knownHostsFileName);
        this.connectionPort = connectionPort;
        this.timeOut = 60000;
    }

    public SSHHandler(String userName, String password, String connectionIp, String knownHostsFileName, int connectionPort, int timeOut){
        initActions(userName, password, connectionIp, knownHostsFileName);
        this.connectionPort = connectionPort;
        this.timeOut = timeOut;
    }

    public String connect(){
        String errorMessage = null;
        try{
            if(userName != null){
                connectionSession = jschChannel.getSession(userName, connectionIp, connectionPort);
            } else {
                connectionSession = jschChannel.getSession(connectionIp);
            }

            connectionSession.setPassword(password);
            // this for testing only
            connectionSession.setConfig("StrictHostKeyChecking","no");
            connectionSession.connect(timeOut);
        }catch (JSchException e){
            errorMessage = e.getMessage();
        }
        return errorMessage;
    }

    public String sendCommand(String command){
        StringBuilder buffer = new StringBuilder();
        try{
            Channel channel = connectionSession.openChannel("exec");
            ((ChannelExec) channel).setCommand(command);
            InputStream commandOutput = channel.getInputStream();
            channel.connect();
            int readByte = commandOutput.read();
            while(readByte != 0xffffffff){
                buffer.append((char) readByte);
                readByte = commandOutput.read();
            }
            channel.disconnect();
        }catch (Exception e){
            logWarning(e.getMessage());
            return null;
        }
        return buffer.toString();
    }

    public void close(){
        connectionSession.disconnect();
    }

    private void initActions(String username, String password, String connectionIp, String knownHostsFileName){
        jschChannel = new JSch();
        try {
            jschChannel.setKnownHosts(knownHostsFileName);
        } catch (JSchException e){
            logError(e.getMessage());
        }
        this.userName = username;
        this.password = password;
        this.connectionIp = connectionIp;
    }

    private void logError(String errorMessage){
        if(errorMessage != null){
            LOG.log(Level.SEVERE, "{0}:{1} - {2}", new Object[]{connectionIp, connectionPort, errorMessage});
        }
    }

    private void logWarning(String warnMessage){
        if(warnMessage != null){
            LOG.log(Level.WARNING, "{0}:{1} - {2}", new Object[]{connectionIp, connectionPort, warnMessage});
        }
    }

}
