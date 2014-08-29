/**
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.package
 * cloudExplorer
 *
 */
package cloudExplorer;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.Upload;
import java.io.File;
import javax.activation.MimetypesFileTypeMap;
import static cloudExplorer.NewJFrame.jTextArea1;
import com.amazonaws.services.s3.model.StorageClass;

public class Put implements Runnable {

    String message = null;
    NewJFrame mainFrame;
    public static boolean isRunning = true;
    String what = null;
    String access_key = null;
    String bucket = null;
    String endpoint = null;
    String ObjectKey = null;
    String secret_key = null;
    Boolean rrs = false;
    Thread put;
    Boolean encrypt = false;
    public static Boolean running = true;

    public void calibrate() {
        try {
            jTextArea1.setCaretPosition(jTextArea1.getLineStartOffset(jTextArea1.getLineCount() - 1));
        } catch (Exception e) {
        }
    }

    Put(String Awhat, String Aaccess_key, String Asecret_key, String Abucket, String Aendpoint, String AObjectKey, Boolean Arrs, Boolean Aencrypt) {
        what = Awhat;
        access_key = Aaccess_key;
        secret_key = Asecret_key;
        bucket = Abucket;
        endpoint = Aendpoint;
        ObjectKey = AObjectKey;
        rrs = Arrs;
        encrypt = Aencrypt;
    }

    public void run() {
        try {
            AWSCredentials credentials = new BasicAWSCredentials(access_key, secret_key);
            AmazonS3 s3Client = new AmazonS3Client(credentials);
            s3Client.setEndpoint(endpoint);
            TransferManager tx = new TransferManager(s3Client);
            File file = new File(what);
            PutObjectRequest putRequest;
            if (!rrs) {
                putRequest = new PutObjectRequest(bucket, ObjectKey, file);
            } else {
                putRequest = new PutObjectRequest(bucket, ObjectKey, file).withStorageClass(StorageClass.ReducedRedundancy);
            }
            MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();
            String mimeType = mimeTypesMap.getContentType(file);
            mimeType = mimeTypesMap.getContentType(file);
            ObjectMetadata objectMetadata = new ObjectMetadata();
            if (encrypt) {
                objectMetadata.setSSEAlgorithm(ObjectMetadata.AES_256_SERVER_SIDE_ENCRYPTION);
            }
            if ((ObjectKey.contains(".html")) || ObjectKey.contains(".txt")) {
                objectMetadata.setContentType("text/html");
            } else {
                objectMetadata.setContentType(mimeType);
            }
            long t1 = System.currentTimeMillis();
            putRequest.setMetadata(objectMetadata);
            Upload myUpload = tx.upload(putRequest);
            myUpload.waitForCompletion();
            tx.shutdownNow();
            long t2 = System.currentTimeMillis();
            long diff = t2 - t1;
            mainFrame.jTextArea1.append("\nUploaded object: " + ObjectKey + " in " + diff / 1000 + " second(s).");
        } catch (Exception manager) {
        }

        calibrate();
    }

    void startc(String Awhat, String Aaccess_key, String Asecret_key, String Abucket, String Aendpoint, String AObjectKey, Boolean Arrs, Boolean Aencrypt) {

        put = new Thread(new Put(Awhat, Aaccess_key, Asecret_key, Abucket, Aendpoint, AObjectKey, Arrs, Aencrypt));
        put.start();
    }

    void stop() {
        put.stop();
        mainFrame.jTextArea1.append("\nUpload completed or aborted");
        calibrate();
    }

}
