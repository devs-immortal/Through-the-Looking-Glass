package azzy.fabric.lookingglass;

import net.minecraft.client.texture.AbstractTexture;

import java.net.URL;
import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class CacheCrimes {

    private static volatile HashMap<String, CompletableFuture<AbstractTexture>> futureCache;

    public static void init(){
        futureCache = new HashMap<>();
    }

    public static Optional<CompletableFuture<AbstractTexture>> getFuture(String url){
        if(futureCache.containsKey(url))
            return Optional.of(futureCache.get(url));
        return Optional.empty();
    }

    public static void putFuture(String url, CompletableFuture<AbstractTexture> future){
        futureCache.put(url, future);
    }
}
