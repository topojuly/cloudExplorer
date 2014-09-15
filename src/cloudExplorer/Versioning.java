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
import com.amazonaws.services.s3.model.S3VersionSummary;
import com.amazonaws.services.s3.model.VersionListing;
import java.util.List;

public class Versioning {

    NewJFrame mainFrame;
    Delete del;
    public static Boolean delete = false;

    public Versioning(NewJFrame Frame) {
        mainFrame = Frame;
    }

    void getVersions(String key, String access_key, String secret_key, String bucket, String endpoint) {
        String results = null;
        try {
            mainFrame.jTextArea1.append("\nPlease wait, loading versions.");
            mainFrame.calibrateTextArea();
            AWSCredentials credentials = new BasicAWSCredentials(access_key, secret_key);
            AmazonS3 s3Client = new AmazonS3Client(credentials);
            s3Client.setEndpoint(endpoint);

            VersionListing vListing;
            if (key == null) {
                vListing = s3Client.listVersions(bucket, null);
            } else {
                vListing = s3Client.listVersions(bucket, key);
            }

            List<S3VersionSummary> summary = vListing.getVersionSummaries();

            for (S3VersionSummary object : summary) {
                if (!Versioning.delete) {
                    mainFrame.versioning_date.add(object.getLastModified().toString());
                }
                mainFrame.versioning_id.add(object.getVersionId());
                mainFrame.versioning_name.add(object.getKey());
                System.gc();
            }

            if (Versioning.delete) {
                int i = 0;
                for (String delVer : mainFrame.versioning_name) {
                    del = new Delete(delVer, mainFrame.cred.getAccess_key(), mainFrame.cred.getSecret_key(), mainFrame.cred.getBucket(), mainFrame.cred.getEndpoint(), mainFrame.versioning_id.get(i));
                    del.startc(delVer, mainFrame.cred.getAccess_key(), mainFrame.cred.getSecret_key(), mainFrame.cred.getBucket(), mainFrame.cred.getEndpoint(), mainFrame.versioning_id.get(i));
                    long t1 = System.currentTimeMillis();
                    long diff = 0;
                    while (diff < 100) {
                        long t2 = System.currentTimeMillis();
                        diff = t2 - t1;
                    }
                    i++;
                }

            }
            if (Versioning.delete) {
                mainFrame.jTextArea1.append("\nCompleted deleting every object. Please observe this window for any tasks that are still running.");
            } else {
                mainFrame.jTextArea1.append("\nDone gathering Versions.");
            }
            mainFrame.calibrateTextArea();
        } catch (Exception getVersions) {

        }
        if (Versioning.delete) {
            Versioning.delete = false;
            mainFrame.reloadObjects();
        }

    }

}
