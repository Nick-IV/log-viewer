package com.logviewer.services;

import com.logviewer.api.LvPermalinkStorage;
import com.logviewer.data2.config.ConfigDirHolder;
import com.logviewer.domain.Permalink;
import com.logviewer.utils.LvGsonUtils;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.DigestUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class LvPermalinkStorageImpl implements LvPermalinkStorage {

    private final ConfigDirHolder environment;

    public LvPermalinkStorageImpl(ConfigDirHolder environment) {
        this.environment = environment;
    }

    private Path getPermalinkDir() {
        return environment.getConfigDir().resolve("permalinks");
    }

    @Override
    public String save(@Nullable String hash, @NonNull Permalink link) throws IOException {
        Path permalinksDir = getPermalinkDir();
        if (!Files.isDirectory(permalinksDir)) {
            Files.createDirectory(permalinksDir);
        }

        byte[] json = LvGsonUtils.GSON.toJson(link).getBytes(StandardCharsets.UTF_8);

        if (hash == null) {
            hash = DigestUtils.md5DigestAsHex(json).substring(0, 10);
        }

        Files.write(permalinksDir.resolve(hash + ".link"), json);

        return hash;
    }

    @Override
    public Permalink load(String hash) throws IOException {
        Path permalinkFile = getPermalinkDir().resolve(hash + ".link");

        try (BufferedReader reader = Files.newBufferedReader(permalinkFile, StandardCharsets.UTF_8)) {
            return LvGsonUtils.GSON.fromJson(reader, Permalink.class);
        }
    }

}
