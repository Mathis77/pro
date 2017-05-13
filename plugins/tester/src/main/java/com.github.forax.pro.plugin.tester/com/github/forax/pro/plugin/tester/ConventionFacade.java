package com.github.forax.pro.plugin.tester;

import java.nio.file.Path;
import java.util.List;

import com.github.forax.pro.api.TypeCheckedConfig;

@TypeCheckedConfig
public interface ConventionFacade {
  Path pluginDir();
  List<Path> javaModuleExplodedTestPath();
}
