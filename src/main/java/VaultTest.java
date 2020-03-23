import com.bettercloud.vault.Vault;
import com.bettercloud.vault.VaultConfig;
import com.bettercloud.vault.VaultException;
import com.bettercloud.vault.response.AuthResponse;

import java.util.HashMap;
import java.util.Map;

public class VaultTest {
    public static void main(String[] args) throws VaultException {
        final String roleId = "916d2871-5d1b-c30d-34fc-90e43e69b9a2";
        final String secretId = "2ff04268-cc24-4422-e93c-c3f1a8cb7dc4";
        final String role;
        final String jwt;

        final VaultConfig config = new VaultConfig()
                .address("http://127.0.0.1:8200")
                .token(getTokenByAppRole(roleId, secretId))
//                .token(getTokenByGCP(role, jwt))
                .build();
        final Vault vault = new Vault(config);

        Map<String, Object> secrets = new HashMap<>();
        secrets.put("foo", "bar");

        vault.logical().write("secret/java", secrets);

        final String fooValue = vault.logical()
                .read("secret/java")
                .getData().get("foo");
        final String buzzValue = vault.logical()
                .read("secret/manual")
                .getData().get("buzz");

        System.out.println("secret/java foo = " + fooValue);
        System.out.println("secret/java buzz = " + buzzValue);
    }

    // get token using approle authentication
    public static String getTokenByAppRole(String roleId, String secretId) throws VaultException {
        final VaultConfig config = new VaultConfig()
                .address("http://127.0.0.1:8200")
                .build();
        final Vault vault = new Vault(config);
        final AuthResponse response = vault.auth().loginByAppRole("approle", roleId, secretId);
        return response.getAuthClientToken();
    }

    // get token using gcp authentication
    public static String getTokenByGCP(String role, String jwt) throws VaultException {
        final VaultConfig config = new VaultConfig()
                .address("http://127.0.0.1:8200")
                .build();
        final Vault vault = new Vault(config);
        final AuthResponse response = vault.auth().loginByGCP(role, jwt);
        return response.getAuthClientToken();
    }
}