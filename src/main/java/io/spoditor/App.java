package io.spoditor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.kubernetes.client.extended.kubectl.Kubectl;
import io.kubernetes.client.extended.kubectl.exception.KubectlException;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.util.Config;

/**
 * Tail and follow the log of the kube-apiserver container in kube-apiserver-kind-control-plan pod on a KinD cluster
 * 
 * execute "kind create cluster" to prepare a cluster first
 */
public class App {
    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
        try {
            Configuration.setDefaultApiClient(Config.defaultClient());
        } catch (IOException e) {
            LOGGER.error("failed to create Kubernetes Java Client", e);
        }

        // Kubernetes Java client log API implicitly "follows" the log stream
        try (BufferedReader br = new BufferedReader(new InputStreamReader(Kubectl.log()
                .namespace("kube-system")
                .name("kube-apiserver-kind-control-plane")
                .container("kube-apiserver")
                .execute()))) {
            String line = null;
            while ((line = br.readLine()) != null) {
                LOGGER.info("log from container: {}", line);
            }
        } catch (KubectlException | IOException e) {
            LOGGER.error("failed to execute kubectl log", e);
        }
    }
}
