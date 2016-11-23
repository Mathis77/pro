package com.github.forax.pro.api.impl;

import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;

import com.github.forax.pro.api.Config;
import com.github.forax.pro.api.MutableConfig;

public class DefaultConfig implements MutableConfig {
  private final ThreadLocal<Object> root = new ThreadLocal<>();
  
  public DefaultConfig(Object root) {
    this.root.set(Objects.requireNonNull(root));
  }

  @Override
  public String toString() {
    return root.get().toString();
  }
  
  @Override
  public <T> Optional<T> get(String key, Class<T> type) {
    return Configs.get(root.get(), key, type, true);
  }
  
  @Override
  public <T> T getOrUpdate(String key, Class<T> type) {
    return Configs.get(root.get(), key, type, false).get();
  }
  
  @Override
  public void set(String key, Object value) {
    Configs.set(root.get(), key, value);
  }
  
  @Override
  public void forEach(String key, BiConsumer<String, Object> consumer) {
     Configs.forEach(root.get(), key, consumer); 
  }
  
  public void enter(Runnable runnable) {
    Object oldRoot = root.get();
    Object newRoot = Configs.duplicate(oldRoot);
    root.set(newRoot);
    try {
      runnable.run();
    } finally {
      root.set(oldRoot);
    }
  }
  
  public MutableConfig asChecked(String prefix) {
    return new MutableConfig() {
      @Override
      public <T> T getOrUpdate(String key, Class<T> type) {
        if (!key.startsWith(prefix + ".") && !key.equals(prefix)) {
          throw new IllegalArgumentException("a plugin can only set a key starting by its name");
        }
        return DefaultConfig.this.getOrUpdate(key, type);
      }
      
      @Override
      public <T> Optional<T> get(String key, Class<T> type) {
        return DefaultConfig.this.get(key, type);
      }
      
      @Override
      public void set(String key, Object object) {
        if (!key.startsWith(prefix + ".")) {
          throw new IllegalArgumentException("a plugin can only set a key starting by its name");
        }
        DefaultConfig.this.set(key, object);
      }
      
      @Override
      public void forEach(String key, BiConsumer<String, Object> consumer) {
        DefaultConfig.this.forEach(key, consumer);
      }
      
      @Override
      public String toString() {
        return DefaultConfig.this.toString();
      }
    };
  }
  
  public Config asConfig() {
    return new Config() {
      @Override
      public <T> Optional<T> get(String key, Class<T> type) {
        return DefaultConfig.this.get(key, type);
      }
      
      @Override
      public void forEach(String key, BiConsumer<String, Object> consumer) {
        DefaultConfig.this.forEach(key, consumer);
      }
      
      @Override
      public String toString() {
        return DefaultConfig.this.toString();
      }
    };
  }
}