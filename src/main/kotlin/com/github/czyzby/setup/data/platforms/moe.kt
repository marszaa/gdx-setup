package com.github.czyzby.setup.data.platforms

import com.github.czyzby.setup.data.files.CopiedFile
import com.github.czyzby.setup.data.files.SourceFile
import com.github.czyzby.setup.data.files.path
import com.github.czyzby.setup.data.gradle.GradleFile
import com.github.czyzby.setup.data.project.Project
import com.github.czyzby.setup.views.GdxPlatform

/**
 * Represents iOS MOE backend.
 * @author Kotcrab
 */
@GdxPlatform
class MOE : Platform {
    companion object {
        const val ID = "ios-moe"
    }

    override val id = ID
    override val isGraphical = false // Will not be selected as LibGDX client platform. iOS is the default one.

    override fun createGradleFile(project: Project): GradleFile = MOEGradleFile(project)

    override fun initiate(project: Project) {
        project.rootGradle.buildDependencies.add("\"org.multi-os-engine:moe-gradle:\$moeVersion\"")
        project.properties["moeVersion"] = project.advanced.moeVersion

        arrayOf("Default.png", "Default@2x.png", "Default@2x~ipad.png", "Default-375w-667h@2x.png",
                "Default-414w-736h@3x.png", "Default-568h@2x.png", "Default-1024w-1366h@2x~ipad.png", "Default~ipad.png",
                "Icon.png", "Icon@2x.png", "Icon-72.png", "Icon-72@2x.png").forEach {
            project.files.add(CopiedFile(projectName = ID, path = path("xcode", "ios-moe", it),
                    original = path("generator", "ios", "data", it)))
        }

        arrayOf("custom.xcconfig", "Info.plist", "main.cpp").forEach {
            project.files.add(CopiedFile(projectName = ID, path = path("xcode", "ios-moe", it),
                    original = path("generator", "ios-moe", "xcode", "ios-moe", it)))
        }

        arrayOf("Info.plist", "main.cpp").forEach {
            project.files.add(CopiedFile(projectName = ID, path = path("xcode", "ios-moe-Test", it),
                    original = path("generator", "ios-moe", "xcode", "ios-moe-Test", it)))
        }

        project.files.add(MOEXCodeProjectFile(project))
    }
}

class MOEGradleFile(val project: Project) : GradleFile(MOE.ID) {
    init {
        dependencies.add("project(':${Core.ID}')")
        addDependency("com.badlogicgames.gdx:gdx-backend-moe:\$gdxVersion")
        addDependency("com.badlogicgames.gdx:gdx-platform:\$gdxVersion:natives-ios")
    }

    override fun getContent() = """apply plugin: 'moe'

// Exclude all files from Gradle's test runner
test { exclude '**' }

task copyNatives << {
    file("xcode/native/ios/").mkdirs();
    def LD_FLAGS = "LIBGDX_NATIVES = "
    configurations.natives.files.each { jar->
        def outputDir = null
        if (jar.name.endsWith("natives-ios.jar")) outputDir = file("xcode/native/ios")
        if (outputDir != null) {
            FileCollection fileCollection = zipTree(jar)
            for (File libFile : fileCollection) {
                if (libFile.getAbsolutePath().endsWith(".a") && !libFile.getAbsolutePath().contains("/tvos/")) {
                    copy {
                        from libFile.getAbsolutePath()
                        into outputDir
                    }
                    LD_FLAGS += " -force_load \${'$'}{SRCROOT}/native/ios/" + libFile.getName()
                }
            }
        }
    }
    def outFlags = file("xcode/ios-moe/custom.xcconfig");
    outFlags.write LD_FLAGS

    def proguard = file("proguard.append.cfg")
    if (!proguard.exists()) {
        proguard = new File("proguard.append.cfg")
        proguard << "\n-keep class com.badlogic.** { *; }\n"
        proguard << "-keep enum com.badlogic.** { *; }\n"
    }
}

sourceSets.main.java.srcDirs = [ "src/" ]
sourceSets.main.resources.srcDirs = [ file("../assets") ]

// Setup Multi-OS Engine
moe {
    xcode {
        project 'xcode/ios-moe.xcodeproj'
        mainTarget 'ios-moe'
        testTarget 'ios-moe-Test'
    }
}

moeMainReleaseIphoneosXcodeBuild.dependsOn copyNatives
moeMainDebugIphoneosXcodeBuild.dependsOn copyNatives
moeMainReleaseIphonesimulatorXcodeBuild.dependsOn copyNatives
moeMainDebugIphonesimulatorXcodeBuild.dependsOn copyNatives

// Setup Eclipse
eclipse {
    // Set Multi-OS Engine nature
    project {
        natures 'org.multi-os-engine.project'
    }
}

dependencies {
  configurations { natives }

${joinDependencies(dependencies)}}
"""

}


class MOEXCodeProjectFile(val project: Project) : SourceFile(projectName = MOE.ID, sourceFolderPath = path("xcode", "ios-moe.xcodeproj"), fileName = "project.pbxproj",
        packageName = "", content = """// !${'$'}*UTF8*${'$'}!
{
	archiveVersion = 1;
	classes = {
	};
	objectVersion = 46;
	objects = {

/* Begin PBXBuildFile section */
		093EA0331288EEC18D3C79EC /* main.cpp in Sources */ = {isa = PBXBuildFile; fileRef = 1E152A74253836079FA1075F /* main.cpp */; };
		16479D2CDCAC394B9A9722FE /* main.cpp in Sources */ = {isa = PBXBuildFile; fileRef = 8084D1F0A307440EC73CBFCA /* main.cpp */; };
		581773911E37A0F0004E28A9 /* Default-375w-667h@2x.png in Resources */ = {isa = PBXBuildFile; fileRef = 581773851E37A0F0004E28A9 /* Default-375w-667h@2x.png */; };
		581773921E37A0F0004E28A9 /* Default-414w-736h@3x.png in Resources */ = {isa = PBXBuildFile; fileRef = 581773861E37A0F0004E28A9 /* Default-414w-736h@3x.png */; };
		581773931E37A0F0004E28A9 /* Default-568h@2x.png in Resources */ = {isa = PBXBuildFile; fileRef = 581773871E37A0F0004E28A9 /* Default-568h@2x.png */; };
		581773941E37A0F0004E28A9 /* Default-1024w-1366h@2x~ipad.png in Resources */ = {isa = PBXBuildFile; fileRef = 581773881E37A0F0004E28A9 /* Default-1024w-1366h@2x~ipad.png */; };
		581773951E37A0F0004E28A9 /* Default.png in Resources */ = {isa = PBXBuildFile; fileRef = 581773891E37A0F0004E28A9 /* Default.png */; };
		581773961E37A0F0004E28A9 /* Default@2x.png in Resources */ = {isa = PBXBuildFile; fileRef = 5817738A1E37A0F0004E28A9 /* Default@2x.png */; };
		581773971E37A0F0004E28A9 /* Default@2x~ipad.png in Resources */ = {isa = PBXBuildFile; fileRef = 5817738B1E37A0F0004E28A9 /* Default@2x~ipad.png */; };
		581773981E37A0F0004E28A9 /* Default~ipad.png in Resources */ = {isa = PBXBuildFile; fileRef = 5817738C1E37A0F0004E28A9 /* Default~ipad.png */; };
		581773991E37A0F0004E28A9 /* Icon-72.png in Resources */ = {isa = PBXBuildFile; fileRef = 5817738D1E37A0F0004E28A9 /* Icon-72.png */; };
		5817739A1E37A0F0004E28A9 /* Icon-72@2x.png in Resources */ = {isa = PBXBuildFile; fileRef = 5817738E1E37A0F0004E28A9 /* Icon-72@2x.png */; };
		5817739B1E37A0F0004E28A9 /* Icon.png in Resources */ = {isa = PBXBuildFile; fileRef = 5817738F1E37A0F0004E28A9 /* Icon.png */; };
		5817739C1E37A0F0004E28A9 /* Icon@2x.png in Resources */ = {isa = PBXBuildFile; fileRef = 581773901E37A0F0004E28A9 /* Icon@2x.png */; };
		581773A61E37A2DB004E28A9 /* AudioToolbox.framework in Frameworks */ = {isa = PBXBuildFile; fileRef = 5817739F1E37A2DB004E28A9 /* AudioToolbox.framework */; };
		581773A71E37A2DB004E28A9 /* AVFoundation.framework in Frameworks */ = {isa = PBXBuildFile; fileRef = 581773A01E37A2DB004E28A9 /* AVFoundation.framework */; };
		581773A81E37A2DB004E28A9 /* CoreGraphics.framework in Frameworks */ = {isa = PBXBuildFile; fileRef = 581773A11E37A2DB004E28A9 /* CoreGraphics.framework */; };
		581773A91E37A2DB004E28A9 /* CoreMotion.framework in Frameworks */ = {isa = PBXBuildFile; fileRef = 581773A21E37A2DB004E28A9 /* CoreMotion.framework */; };
		581773AA1E37A2DB004E28A9 /* OpenAL.framework in Frameworks */ = {isa = PBXBuildFile; fileRef = 581773A31E37A2DB004E28A9 /* OpenAL.framework */; };
		581773AB1E37A2DB004E28A9 /* OpenGLES.framework in Frameworks */ = {isa = PBXBuildFile; fileRef = 581773A41E37A2DB004E28A9 /* OpenGLES.framework */; };
		581773AC1E37A2DB004E28A9 /* QuartzCore.framework in Frameworks */ = {isa = PBXBuildFile; fileRef = 581773A51E37A2DB004E28A9 /* QuartzCore.framework */; };
		581773AE1E37A7EF004E28A9 /* UIKit.framework in Frameworks */ = {isa = PBXBuildFile; fileRef = 581773AD1E37A7EF004E28A9 /* UIKit.framework */; };
/* End PBXBuildFile section */

/* Begin PBXFileReference section */
		1E152A74253836079FA1075F /* main.cpp */ = {isa = PBXFileReference; lastKnownFileType = sourcecode.cpp.cpp; path = main.cpp; sourceTree = "<group>"; };
		581773851E37A0F0004E28A9 /* Default-375w-667h@2x.png */ = {isa = PBXFileReference; lastKnownFileType = image.png; path = "Default-375w-667h@2x.png"; sourceTree = "<group>"; };
		581773861E37A0F0004E28A9 /* Default-414w-736h@3x.png */ = {isa = PBXFileReference; lastKnownFileType = image.png; path = "Default-414w-736h@3x.png"; sourceTree = "<group>"; };
		581773871E37A0F0004E28A9 /* Default-568h@2x.png */ = {isa = PBXFileReference; lastKnownFileType = image.png; path = "Default-568h@2x.png"; sourceTree = "<group>"; };
		581773881E37A0F0004E28A9 /* Default-1024w-1366h@2x~ipad.png */ = {isa = PBXFileReference; lastKnownFileType = image.png; path = "Default-1024w-1366h@2x~ipad.png"; sourceTree = "<group>"; };
		581773891E37A0F0004E28A9 /* Default.png */ = {isa = PBXFileReference; lastKnownFileType = image.png; path = Default.png; sourceTree = "<group>"; };
		5817738A1E37A0F0004E28A9 /* Default@2x.png */ = {isa = PBXFileReference; lastKnownFileType = image.png; path = "Default@2x.png"; sourceTree = "<group>"; };
		5817738B1E37A0F0004E28A9 /* Default@2x~ipad.png */ = {isa = PBXFileReference; lastKnownFileType = image.png; path = "Default@2x~ipad.png"; sourceTree = "<group>"; };
		5817738C1E37A0F0004E28A9 /* Default~ipad.png */ = {isa = PBXFileReference; lastKnownFileType = image.png; path = "Default~ipad.png"; sourceTree = "<group>"; };
		5817738D1E37A0F0004E28A9 /* Icon-72.png */ = {isa = PBXFileReference; lastKnownFileType = image.png; path = "Icon-72.png"; sourceTree = "<group>"; };
		5817738E1E37A0F0004E28A9 /* Icon-72@2x.png */ = {isa = PBXFileReference; lastKnownFileType = image.png; path = "Icon-72@2x.png"; sourceTree = "<group>"; };
		5817738F1E37A0F0004E28A9 /* Icon.png */ = {isa = PBXFileReference; lastKnownFileType = image.png; path = Icon.png; sourceTree = "<group>"; };
		581773901E37A0F0004E28A9 /* Icon@2x.png */ = {isa = PBXFileReference; lastKnownFileType = image.png; path = "Icon@2x.png"; sourceTree = "<group>"; };
		5817739D1E37A10D004E28A9 /* custom.xcconfig */ = {isa = PBXFileReference; lastKnownFileType = text.xcconfig; path = custom.xcconfig; sourceTree = "<group>"; };
		5817739F1E37A2DB004E28A9 /* AudioToolbox.framework */ = {isa = PBXFileReference; lastKnownFileType = wrapper.framework; name = AudioToolbox.framework; path = System/Library/Frameworks/AudioToolbox.framework; sourceTree = SDKROOT; };
		581773A01E37A2DB004E28A9 /* AVFoundation.framework */ = {isa = PBXFileReference; lastKnownFileType = wrapper.framework; name = AVFoundation.framework; path = System/Library/Frameworks/AVFoundation.framework; sourceTree = SDKROOT; };
		581773A11E37A2DB004E28A9 /* CoreGraphics.framework */ = {isa = PBXFileReference; lastKnownFileType = wrapper.framework; name = CoreGraphics.framework; path = System/Library/Frameworks/CoreGraphics.framework; sourceTree = SDKROOT; };
		581773A21E37A2DB004E28A9 /* CoreMotion.framework */ = {isa = PBXFileReference; lastKnownFileType = wrapper.framework; name = CoreMotion.framework; path = System/Library/Frameworks/CoreMotion.framework; sourceTree = SDKROOT; };
		581773A31E37A2DB004E28A9 /* OpenAL.framework */ = {isa = PBXFileReference; lastKnownFileType = wrapper.framework; name = OpenAL.framework; path = System/Library/Frameworks/OpenAL.framework; sourceTree = SDKROOT; };
		581773A41E37A2DB004E28A9 /* OpenGLES.framework */ = {isa = PBXFileReference; lastKnownFileType = wrapper.framework; name = OpenGLES.framework; path = System/Library/Frameworks/OpenGLES.framework; sourceTree = SDKROOT; };
		581773A51E37A2DB004E28A9 /* QuartzCore.framework */ = {isa = PBXFileReference; lastKnownFileType = wrapper.framework; name = QuartzCore.framework; path = System/Library/Frameworks/QuartzCore.framework; sourceTree = SDKROOT; };
		581773AD1E37A7EF004E28A9 /* UIKit.framework */ = {isa = PBXFileReference; lastKnownFileType = wrapper.framework; name = UIKit.framework; path = System/Library/Frameworks/UIKit.framework; sourceTree = SDKROOT; };
		5863B87B1DA682C800E46B6A /* ios-moe-Test.app */ = {isa = PBXFileReference; explicitFileType = wrapper.application; includeInIndex = 0; path = "ios-moe-Test.app"; sourceTree = BUILT_PRODUCTS_DIR; };
		5863B88E1DA682C800E46B6A /* Info.plist */ = {isa = PBXFileReference; lastKnownFileType = text.plist.xml; path = Info.plist; sourceTree = "<group>"; };
		58C6F5241DA66CB600309CB6 /* ${project.basic.name}.app */ = {isa = PBXFileReference; explicitFileType = wrapper.application; includeInIndex = 0; path = "${project.basic.name}.app"; sourceTree = BUILT_PRODUCTS_DIR; };
		58C6F5381DA66CB600309CB6 /* Info.plist */ = {isa = PBXFileReference; lastKnownFileType = text.plist.xml; path = Info.plist; sourceTree = "<group>"; };
		8084D1F0A307440EC73CBFCA /* main.cpp */ = {isa = PBXFileReference; lastKnownFileType = sourcecode.cpp.cpp; path = main.cpp; sourceTree = "<group>"; };
/* End PBXFileReference section */

/* Begin PBXFrameworksBuildPhase section */
		5863B8781DA682C800E46B6A /* Frameworks */ = {
			isa = PBXFrameworksBuildPhase;
			buildActionMask = 2147483647;
			files = (
			);
			runOnlyForDeploymentPostprocessing = 0;
		};
		58C6F5211DA66CB600309CB6 /* Frameworks */ = {
			isa = PBXFrameworksBuildPhase;
			buildActionMask = 2147483647;
			files = (
				581773A61E37A2DB004E28A9 /* AudioToolbox.framework in Frameworks */,
				581773A71E37A2DB004E28A9 /* AVFoundation.framework in Frameworks */,
				581773A81E37A2DB004E28A9 /* CoreGraphics.framework in Frameworks */,
				581773A91E37A2DB004E28A9 /* CoreMotion.framework in Frameworks */,
				581773AA1E37A2DB004E28A9 /* OpenAL.framework in Frameworks */,
				581773AB1E37A2DB004E28A9 /* OpenGLES.framework in Frameworks */,
				581773AC1E37A2DB004E28A9 /* QuartzCore.framework in Frameworks */,
				581773AE1E37A7EF004E28A9 /* UIKit.framework in Frameworks */,
			);
			runOnlyForDeploymentPostprocessing = 0;
		};
/* End PBXFrameworksBuildPhase section */

/* Begin PBXGroup section */
		5817739E1E37A2DA004E28A9 /* Frameworks */ = {
			isa = PBXGroup;
			children = (
				581773AD1E37A7EF004E28A9 /* UIKit.framework */,
				5817739F1E37A2DB004E28A9 /* AudioToolbox.framework */,
				581773A01E37A2DB004E28A9 /* AVFoundation.framework */,
				581773A11E37A2DB004E28A9 /* CoreGraphics.framework */,
				581773A21E37A2DB004E28A9 /* CoreMotion.framework */,
				581773A31E37A2DB004E28A9 /* OpenAL.framework */,
				581773A41E37A2DB004E28A9 /* OpenGLES.framework */,
				581773A51E37A2DB004E28A9 /* QuartzCore.framework */,
			);
			name = Frameworks;
			sourceTree = "<group>";
		};
		5863B87C1DA682C800E46B6A /* ios-moe-Test */ = {
			isa = PBXGroup;
			children = (
				5863B88E1DA682C800E46B6A /* Info.plist */,
				5863B87D1DA682C800E46B6A /* Supporting Files */,
			);
			path = "ios-moe-Test";
			sourceTree = "<group>";
		};
		5863B87D1DA682C800E46B6A /* Supporting Files */ = {
			isa = PBXGroup;
			children = (
				1E152A74253836079FA1075F /* main.cpp */,
			);
			name = "Supporting Files";
			sourceTree = "<group>";
		};
		58C6F51B1DA66CB600309CB6 = {
			isa = PBXGroup;
			children = (
				58C6F5261DA66CB600309CB6 /* ios-moe */,
				5863B87C1DA682C800E46B6A /* ios-moe-Test */,
				58C6F5251DA66CB600309CB6 /* Products */,
				5817739E1E37A2DA004E28A9 /* Frameworks */,
			);
			sourceTree = "<group>";
		};
		58C6F5251DA66CB600309CB6 /* Products */ = {
			isa = PBXGroup;
			children = (
				58C6F5241DA66CB600309CB6 /* ${project.basic.name}.app */,
				5863B87B1DA682C800E46B6A /* ios-moe-Test.app */,
			);
			name = Products;
			sourceTree = "<group>";
		};
		58C6F5261DA66CB600309CB6 /* ios-moe */ = {
			isa = PBXGroup;
			children = (
				58C6F5381DA66CB600309CB6 /* Info.plist */,
				581773851E37A0F0004E28A9 /* Default-375w-667h@2x.png */,
				581773861E37A0F0004E28A9 /* Default-414w-736h@3x.png */,
				581773871E37A0F0004E28A9 /* Default-568h@2x.png */,
				581773881E37A0F0004E28A9 /* Default-1024w-1366h@2x~ipad.png */,
				581773891E37A0F0004E28A9 /* Default.png */,
				5817738A1E37A0F0004E28A9 /* Default@2x.png */,
				5817738B1E37A0F0004E28A9 /* Default@2x~ipad.png */,
				5817738C1E37A0F0004E28A9 /* Default~ipad.png */,
				5817738D1E37A0F0004E28A9 /* Icon-72.png */,
				5817738E1E37A0F0004E28A9 /* Icon-72@2x.png */,
				5817738F1E37A0F0004E28A9 /* Icon.png */,
				581773901E37A0F0004E28A9 /* Icon@2x.png */,
				5817739D1E37A10D004E28A9 /* custom.xcconfig */,
				58C6F5271DA66CB600309CB6 /* Supporting Files */,
			);
			path = "ios-moe";
			sourceTree = "<group>";
		};
		58C6F5271DA66CB600309CB6 /* Supporting Files */ = {
			isa = PBXGroup;
			children = (
				8084D1F0A307440EC73CBFCA /* main.cpp */,
			);
			name = "Supporting Files";
			sourceTree = "<group>";
		};
/* End PBXGroup section */

/* Begin PBXNativeTarget section */
		5863B87A1DA682C800E46B6A /* ios-moe-Test */ = {
			isa = PBXNativeTarget;
			buildConfigurationList = 5863B88F1DA682C800E46B6A /* Build configuration list for PBXNativeTarget "ios-moe-Test" */;
			buildPhases = (
				1AE79C4EE6E5C399E50B1E3C /* Compile Sources (MOE) */,
				5863B8771DA682C800E46B6A /* Sources */,
				5863B8781DA682C800E46B6A /* Frameworks */,
				5863B8791DA682C800E46B6A /* Resources */,
			);
			buildRules = (
			);
			dependencies = (
			);
			name = "ios-moe-Test";
			productName = "ios-moe-Test";
			productReference = 5863B87B1DA682C800E46B6A /* ios-moe-Test.app */;
			productType = "com.apple.product-type.application";
		};
		58C6F5231DA66CB600309CB6 /* ios-moe */ = {
			isa = PBXNativeTarget;
			buildConfigurationList = 58C6F53B1DA66CB600309CB6 /* Build configuration list for PBXNativeTarget "ios-moe" */;
			buildPhases = (
				F461487E379A46F550F0C10A /* Compile Sources (MOE) */,
				58C6F5201DA66CB600309CB6 /* Sources */,
				58C6F5211DA66CB600309CB6 /* Frameworks */,
				58C6F5221DA66CB600309CB6 /* Resources */,
			);
			buildRules = (
			);
			dependencies = (
			);
			name = "ios-moe";
			productName = "ios-moe";
			productReference = 58C6F5241DA66CB600309CB6 /* ${project.basic.name}.app */;
			productType = "com.apple.product-type.application";
		};
/* End PBXNativeTarget section */

/* Begin PBXProject section */
		58C6F51C1DA66CB600309CB6 /* Project object */ = {
			isa = PBXProject;
			attributes = {
				LastUpgradeCheck = 0820;
				ORGANIZATIONNAME = x;
				TargetAttributes = {
					5863B87A1DA682C800E46B6A = {
						CreatedOnToolsVersion = 7.3.1;
					};
					58C6F5231DA66CB600309CB6 = {
						CreatedOnToolsVersion = 7.3.1;
					};
				};
			};
			buildConfigurationList = 58C6F51F1DA66CB600309CB6 /* Build configuration list for PBXProject "ios-moe" */;
			compatibilityVersion = "Xcode 3.2";
			developmentRegion = English;
			hasScannedForEncodings = 0;
			knownRegions = (
				en,
				Base,
			);
			mainGroup = 58C6F51B1DA66CB600309CB6;
			productRefGroup = 58C6F5251DA66CB600309CB6 /* Products */;
			projectDirPath = "";
			projectRoot = "";
			targets = (
				58C6F5231DA66CB600309CB6 /* ios-moe */,
				5863B87A1DA682C800E46B6A /* ios-moe-Test */,
			);
		};
/* End PBXProject section */

/* Begin PBXResourcesBuildPhase section */
		5863B8791DA682C800E46B6A /* Resources */ = {
			isa = PBXResourcesBuildPhase;
			buildActionMask = 2147483647;
			files = (
			);
			runOnlyForDeploymentPostprocessing = 0;
		};
		58C6F5221DA66CB600309CB6 /* Resources */ = {
			isa = PBXResourcesBuildPhase;
			buildActionMask = 2147483647;
			files = (
				581773961E37A0F0004E28A9 /* Default@2x.png in Resources */,
				5817739C1E37A0F0004E28A9 /* Icon@2x.png in Resources */,
				581773911E37A0F0004E28A9 /* Default-375w-667h@2x.png in Resources */,
				5817739A1E37A0F0004E28A9 /* Icon-72@2x.png in Resources */,
				581773991E37A0F0004E28A9 /* Icon-72.png in Resources */,
				581773971E37A0F0004E28A9 /* Default@2x~ipad.png in Resources */,
				581773941E37A0F0004E28A9 /* Default-1024w-1366h@2x~ipad.png in Resources */,
				581773981E37A0F0004E28A9 /* Default~ipad.png in Resources */,
				581773921E37A0F0004E28A9 /* Default-414w-736h@3x.png in Resources */,
				5817739B1E37A0F0004E28A9 /* Icon.png in Resources */,
				581773931E37A0F0004E28A9 /* Default-568h@2x.png in Resources */,
				581773951E37A0F0004E28A9 /* Default.png in Resources */,
			);
			runOnlyForDeploymentPostprocessing = 0;
		};
/* End PBXResourcesBuildPhase section */

/* Begin PBXShellScriptBuildPhase section */
		1AE79C4EE6E5C399E50B1E3C /* Compile Sources (MOE) */ = {
			isa = PBXShellScriptBuildPhase;
			buildActionMask = 2147483647;
			files = (
			);
			inputPaths = (
			);
			name = "Compile Sources (MOE)";
			outputPaths = (
			);
			runOnlyForDeploymentPostprocessing = 0;
			shellPath = /bin/bash;
			shellScript = "set -e\n\nexport MOE_BUILD_SOURCE_SET=\"test\"\n\n# Check project directory\nif [ ! -d \"${'$'}MOE_PROJECT_DIR\" ]; then\n    echo \"${'$'}0:${'$'}LINENO:1: error: 'MOE_PROJECT_DIR' doesn't point to a directory!\"; exit 1;\nfi\ncd \"${'$'}MOE_PROJECT_DIR\"\n\n# Export JAVA_HOME\nexport JAVA_HOME=${'$'}(/usr/libexec/java_home -v 1.8)\n\n# Utility function for finding the Gradle implementation\nfunction findGradle {\n    CD=\"${'$'}PWD\"\n    while [ \"${'$'}CD\" != \"\" ]; do\n        echo \"Looking for gradlew in ${'$'}CD\"\n        if [ -x \"${'$'}CD/gradlew\" ]; then\n            GRADLE_EXEC=${'$'}CD/gradlew\n            return 0\n        fi\n        CD=\"${'$'}{CD%/*}\"\n    done\n\n    echo \"Checking with 'which'\"\n    GRADLE_EXEC=${'$'}(which 'gradle')\n\n    if [ \"${'$'}GRADLE_EXEC\" = \"\" ]; then\n        echo \"Failed to locate 'gradle' executable!\"\n        exit 1\n    fi\n}\n\n# Build project with Gradle\nif [ -z \"${'$'}MOE_GRADLE_EXTERNAL_BUILD\" ]; then\n    findGradle\n    \"${'$'}GRADLE_EXEC\" --no-daemon moeXcodeInternal -s\nfi\n\n# Check output directory\nif [ ! -d \"${'$'}{MOE_PROJECT_BUILD_DIR}\" ]; then\n    echo \"${'$'}0:${'$'}LINENO:1: error: 'MOE_PROJECT_BUILD_DIR' doesn't point to a directory!\"; exit 1;\nfi\n\n# Copy some resources\nmkdir -p \"${'$'}{TARGET_BUILD_DIR}/${'$'}{UNLOCALIZED_RESOURCES_FOLDER_PATH}\"\ncp \"${'$'}{MOE_PROJECT_BUILD_DIR}/moe/${'$'}{MOE_BUILD_SOURCE_SET}/application.jar\" \"${'$'}{TARGET_BUILD_DIR}/${'$'}{UNLOCALIZED_RESOURCES_FOLDER_PATH}/\"\ncp \"${'$'}{MOE_PROJECT_BUILD_DIR}/moe/${'$'}{MOE_BUILD_SOURCE_SET}/preregister.txt\" \"${'$'}{TARGET_BUILD_DIR}/${'$'}{UNLOCALIZED_RESOURCES_FOLDER_PATH}/\"\nif [ \"${'$'}{MOE_BUILD_SOURCE_SET}\" == \"test\" ]; then\n    cp \"${'$'}{MOE_PROJECT_BUILD_DIR}/moe/${'$'}{MOE_BUILD_SOURCE_SET}/classlist.txt\" \"${'$'}{TARGET_BUILD_DIR}/${'$'}{UNLOCALIZED_RESOURCES_FOLDER_PATH}/\"\nfi\n\n# Copy android CA certificates on demand\nif [ \"${'$'}{MOE_COPY_ANDROID_CACERTS}\" == \"YES\" ]; then\n    rm -rf \"${'$'}{TARGET_BUILD_DIR}/${'$'}{UNLOCALIZED_RESOURCES_FOLDER_PATH}/android_root/etc/security/cacerts\"\n    mkdir -p \"${'$'}{TARGET_BUILD_DIR}/${'$'}{UNLOCALIZED_RESOURCES_FOLDER_PATH}/android_root/etc/security/cacerts\"\n    unzip \"${'$'}{MOE_SDK_PATH}/sdk/moe-core-certificates.zip\" -d \"${'$'}{TARGET_BUILD_DIR}/${'$'}{UNLOCALIZED_RESOURCES_FOLDER_PATH}/android_root/etc/security/cacerts\"\nfi\n\n# Copy and sign MOE framework\nrsync -av --filter \"- CVS/\" --filter \"- .svn/\" --filter \"- .git/\" --filter \"- .hg/\" --filter \"- Headers\" --filter \"- PrivateHeaders\" --filter \"- Modules\" \\\n    \"${'$'}{MOE_FRAMEWORK_PATH}/MOE.framework\" \"${'$'}{TARGET_BUILD_DIR}/${'$'}{FRAMEWORKS_FOLDER_PATH}/\"\nif [ -n \"${'$'}{EXPANDED_CODE_SIGN_IDENTITY}\" -a \"${'$'}{CODE_SIGNING_REQUIRED}\" != \"NO\" -a \"${'$'}{CODE_SIGNING_ALLOWED}\" != \"NO\" ]; then\n    /usr/bin/codesign --force --sign ${'$'}EXPANDED_CODE_SIGN_IDENTITY ${'$'}OTHER_CODE_SIGN_FLAGS \\\n        --preserve-metadata=identifier,entitlements \"${'$'}{TARGET_BUILD_DIR}/${'$'}{FRAMEWORKS_FOLDER_PATH}/MOE.framework\"\nfi\n";
		};
		F461487E379A46F550F0C10A /* Compile Sources (MOE) */ = {
			isa = PBXShellScriptBuildPhase;
			buildActionMask = 2147483647;
			files = (
			);
			inputPaths = (
			);
			name = "Compile Sources (MOE)";
			outputPaths = (
			);
			runOnlyForDeploymentPostprocessing = 0;
			shellPath = /bin/bash;
			shellScript = "set -e\n\nexport MOE_BUILD_SOURCE_SET=\"main\"\n\n# Check project directory\nif [ ! -d \"${'$'}MOE_PROJECT_DIR\" ]; then\n    echo \"${'$'}0:${'$'}LINENO:1: error: 'MOE_PROJECT_DIR' doesn't point to a directory!\"; exit 1;\nfi\ncd \"${'$'}MOE_PROJECT_DIR\"\n\n# Export JAVA_HOME\nexport JAVA_HOME=${'$'}(/usr/libexec/java_home -v 1.8)\n\n# Utility function for finding the Gradle implementation\nfunction findGradle {\n    CD=\"${'$'}PWD\"\n    while [ \"${'$'}CD\" != \"\" ]; do\n        echo \"Looking for gradlew in ${'$'}CD\"\n        if [ -x \"${'$'}CD/gradlew\" ]; then\n            GRADLE_EXEC=${'$'}CD/gradlew\n            return 0\n        fi\n        CD=\"${'$'}{CD%/*}\"\n    done\n\n    echo \"Checking with 'which'\"\n    GRADLE_EXEC=${'$'}(which 'gradle')\n\n    if [ \"${'$'}GRADLE_EXEC\" = \"\" ]; then\n        echo \"Failed to locate 'gradle' executable!\"\n        exit 1\n    fi\n}\n\n# Build project with Gradle\nif [ -z \"${'$'}MOE_GRADLE_EXTERNAL_BUILD\" ]; then\n    findGradle\n    \"${'$'}GRADLE_EXEC\" --no-daemon moeXcodeInternal -s\nfi\n\n# Check output directory\nif [ ! -d \"${'$'}{MOE_PROJECT_BUILD_DIR}\" ]; then\n    echo \"${'$'}0:${'$'}LINENO:1: error: 'MOE_PROJECT_BUILD_DIR' doesn't point to a directory!\"; exit 1;\nfi\n\n# Copy some resources\nmkdir -p \"${'$'}{TARGET_BUILD_DIR}/${'$'}{UNLOCALIZED_RESOURCES_FOLDER_PATH}\"\ncp \"${'$'}{MOE_PROJECT_BUILD_DIR}/moe/${'$'}{MOE_BUILD_SOURCE_SET}/application.jar\" \"${'$'}{TARGET_BUILD_DIR}/${'$'}{UNLOCALIZED_RESOURCES_FOLDER_PATH}/\"\ncp \"${'$'}{MOE_PROJECT_BUILD_DIR}/moe/${'$'}{MOE_BUILD_SOURCE_SET}/preregister.txt\" \"${'$'}{TARGET_BUILD_DIR}/${'$'}{UNLOCALIZED_RESOURCES_FOLDER_PATH}/\"\nif [ \"${'$'}{MOE_BUILD_SOURCE_SET}\" == \"test\" ]; then\n    cp \"${'$'}{MOE_PROJECT_BUILD_DIR}/moe/${'$'}{MOE_BUILD_SOURCE_SET}/classlist.txt\" \"${'$'}{TARGET_BUILD_DIR}/${'$'}{UNLOCALIZED_RESOURCES_FOLDER_PATH}/\"\nfi\n\n# Copy android CA certificates on demand\nif [ \"${'$'}{MOE_COPY_ANDROID_CACERTS}\" == \"YES\" ]; then\n    rm -rf \"${'$'}{TARGET_BUILD_DIR}/${'$'}{UNLOCALIZED_RESOURCES_FOLDER_PATH}/android_root/etc/security/cacerts\"\n    mkdir -p \"${'$'}{TARGET_BUILD_DIR}/${'$'}{UNLOCALIZED_RESOURCES_FOLDER_PATH}/android_root/etc/security/cacerts\"\n    unzip \"${'$'}{MOE_SDK_PATH}/sdk/moe-core-certificates.zip\" -d \"${'$'}{TARGET_BUILD_DIR}/${'$'}{UNLOCALIZED_RESOURCES_FOLDER_PATH}/android_root/etc/security/cacerts\"\nfi\n\n# Copy and sign MOE framework\nrsync -av --filter \"- CVS/\" --filter \"- .svn/\" --filter \"- .git/\" --filter \"- .hg/\" --filter \"- Headers\" --filter \"- PrivateHeaders\" --filter \"- Modules\" \\\n    \"${'$'}{MOE_FRAMEWORK_PATH}/MOE.framework\" \"${'$'}{TARGET_BUILD_DIR}/${'$'}{FRAMEWORKS_FOLDER_PATH}/\"\nif [ -n \"${'$'}{EXPANDED_CODE_SIGN_IDENTITY}\" -a \"${'$'}{CODE_SIGNING_REQUIRED}\" != \"NO\" -a \"${'$'}{CODE_SIGNING_ALLOWED}\" != \"NO\" ]; then\n    /usr/bin/codesign --force --sign ${'$'}EXPANDED_CODE_SIGN_IDENTITY ${'$'}OTHER_CODE_SIGN_FLAGS \\\n        --preserve-metadata=identifier,entitlements \"${'$'}{TARGET_BUILD_DIR}/${'$'}{FRAMEWORKS_FOLDER_PATH}/MOE.framework\"\nfi\n";
		};
/* End PBXShellScriptBuildPhase section */

/* Begin PBXSourcesBuildPhase section */
		5863B8771DA682C800E46B6A /* Sources */ = {
			isa = PBXSourcesBuildPhase;
			buildActionMask = 2147483647;
			files = (
				093EA0331288EEC18D3C79EC /* main.cpp in Sources */,
			);
			runOnlyForDeploymentPostprocessing = 0;
		};
		58C6F5201DA66CB600309CB6 /* Sources */ = {
			isa = PBXSourcesBuildPhase;
			buildActionMask = 2147483647;
			files = (
				16479D2CDCAC394B9A9722FE /* main.cpp in Sources */,
			);
			runOnlyForDeploymentPostprocessing = 0;
		};
/* End PBXSourcesBuildPhase section */

/* Begin XCBuildConfiguration section */
		5863B8901DA682C800E46B6A /* Debug */ = {
			isa = XCBuildConfiguration;
			buildSettings = {
				DEAD_CODE_STRIPPING = NO;
				ENABLE_BITCODE = NO;
				FRAMEWORK_SEARCH_PATHS = (
					"${'$'}(inherited)",
					"${'$'}{MOE_FRAMEWORK_PATH}",
				);
				INFOPLIST_FILE = "ios-moe-Test/Info.plist";
				LD_RUNPATH_SEARCH_PATHS = "${'$'}(inherited) @executable_path/Frameworks";
				MOE_COPY_ANDROID_CACERTS = NO;
				MOE_FRAMEWORK_PATH = "${'$'}{MOE_SDK_PATH}/sdk/${'$'}{PLATFORM_NAME}";
				MOE_OTHER_LDFLAGS = "${'$'}{MOE_SECT_OAT} ${'$'}{MOE_SECT_ART} ${'$'}{MOE_SEGPROT} ${'$'}{MOE_PAGEZERO} ${'$'}{MOE_CUSTOM_OTHER_LDFLAGS} -lstdc++ -framework MOE";
				"MOE_PAGEZERO[sdk=iphoneos*]" = "";
				"MOE_PAGEZERO[sdk=iphonesimulator*]" = "-pagezero_size 4096";
				MOE_PROJECT_BUILD_DIR = "${'$'}{MOE_PROJECT_DIR}/build";
				MOE_PROJECT_DIR = "${'$'}{SRCROOT}/../";
				MOE_SDK_PATH = "${'$'}{MOE_PROJECT_BUILD_DIR}/moe/sdk";
				MOE_SECT_ART = "-sectcreate __ARTDATA __artdata \"${'$'}{MOE_PROJECT_BUILD_DIR}/moe/test/xcode/${'$'}{CONFIGURATION}${'$'}{EFFECTIVE_PLATFORM_NAME}/${'$'}{arch}.art\"";
				MOE_SECT_OAT = "-sectcreate __OATDATA __oatdata \"${'$'}{MOE_PROJECT_BUILD_DIR}/moe/test/xcode/${'$'}{CONFIGURATION}${'$'}{EFFECTIVE_PLATFORM_NAME}/${'$'}{arch}.oat\"";
				"MOE_SEGPROT[sdk=iphoneos*]" = "-segprot __OATDATA rx rx -segprot __ARTDATA rw rw";
				"MOE_SEGPROT[sdk=iphonesimulator*]" = "-segprot __OATDATA rwx rx -segprot __ARTDATA rwx rw";
				ONLY_ACTIVE_ARCH = YES;
				OTHER_LDFLAGS = (
					"${'$'}(inherited)",
					"${'$'}{MOE_OTHER_LDFLAGS}",
				);
				PRODUCT_BUNDLE_IDENTIFIER = "${project.basic.rootPackage}";
				PRODUCT_NAME = "${'$'}(TARGET_NAME)";
				STRIP_STYLE = "non-global";
			};
			name = Debug;
		};
		5863B8911DA682C800E46B6A /* Release */ = {
			isa = XCBuildConfiguration;
			buildSettings = {
				DEAD_CODE_STRIPPING = NO;
				ENABLE_BITCODE = NO;
				FRAMEWORK_SEARCH_PATHS = (
					"${'$'}(inherited)",
					"${'$'}{MOE_FRAMEWORK_PATH}",
				);
				INFOPLIST_FILE = "ios-moe-Test/Info.plist";
				LD_RUNPATH_SEARCH_PATHS = "${'$'}(inherited) @executable_path/Frameworks";
				MOE_COPY_ANDROID_CACERTS = NO;
				MOE_FRAMEWORK_PATH = "${'$'}{MOE_SDK_PATH}/sdk/${'$'}{PLATFORM_NAME}";
				MOE_OTHER_LDFLAGS = "${'$'}{MOE_SECT_OAT} ${'$'}{MOE_SECT_ART} ${'$'}{MOE_SEGPROT} ${'$'}{MOE_PAGEZERO} ${'$'}{MOE_CUSTOM_OTHER_LDFLAGS} -lstdc++ -framework MOE";
				"MOE_PAGEZERO[sdk=iphoneos*]" = "";
				"MOE_PAGEZERO[sdk=iphonesimulator*]" = "-pagezero_size 4096";
				MOE_PROJECT_BUILD_DIR = "${'$'}{MOE_PROJECT_DIR}/build";
				MOE_PROJECT_DIR = "${'$'}{SRCROOT}/../";
				MOE_SDK_PATH = "${'$'}{MOE_PROJECT_BUILD_DIR}/moe/sdk";
				MOE_SECT_ART = "-sectcreate __ARTDATA __artdata \"${'$'}{MOE_PROJECT_BUILD_DIR}/moe/test/xcode/${'$'}{CONFIGURATION}${'$'}{EFFECTIVE_PLATFORM_NAME}/${'$'}{arch}.art\"";
				MOE_SECT_OAT = "-sectcreate __OATDATA __oatdata \"${'$'}{MOE_PROJECT_BUILD_DIR}/moe/test/xcode/${'$'}{CONFIGURATION}${'$'}{EFFECTIVE_PLATFORM_NAME}/${'$'}{arch}.oat\"";
				"MOE_SEGPROT[sdk=iphoneos*]" = "-segprot __OATDATA rx rx -segprot __ARTDATA rw rw";
				"MOE_SEGPROT[sdk=iphonesimulator*]" = "-segprot __OATDATA rwx rx -segprot __ARTDATA rwx rw";
				ONLY_ACTIVE_ARCH = NO;
				OTHER_LDFLAGS = (
					"${'$'}(inherited)",
					"${'$'}{MOE_OTHER_LDFLAGS}",
				);
				PRODUCT_BUNDLE_IDENTIFIER = "${project.basic.rootPackage}";
				PRODUCT_NAME = "${'$'}(TARGET_NAME)";
				STRIP_STYLE = "non-global";
			};
			name = Release;
		};
		58C6F5391DA66CB600309CB6 /* Debug */ = {
			isa = XCBuildConfiguration;
			buildSettings = {
				ALWAYS_SEARCH_USER_PATHS = NO;
				CLANG_ANALYZER_NONNULL = YES;
				CLANG_CXX_LANGUAGE_STANDARD = "gnu++0x";
				CLANG_CXX_LIBRARY = "libc++";
				CLANG_ENABLE_MODULES = YES;
				CLANG_ENABLE_OBJC_ARC = YES;
				CLANG_WARN_BOOL_CONVERSION = YES;
				CLANG_WARN_CONSTANT_CONVERSION = YES;
				CLANG_WARN_DIRECT_OBJC_ISA_USAGE = YES_ERROR;
				CLANG_WARN_EMPTY_BODY = YES;
				CLANG_WARN_ENUM_CONVERSION = YES;
				CLANG_WARN_INFINITE_RECURSION = YES;
				CLANG_WARN_INT_CONVERSION = YES;
				CLANG_WARN_OBJC_ROOT_CLASS = YES_ERROR;
				CLANG_WARN_SUSPICIOUS_MOVE = YES;
				CLANG_WARN_UNREACHABLE_CODE = YES;
				CLANG_WARN__DUPLICATE_METHOD_MATCH = YES;
				"CODE_SIGN_IDENTITY[sdk=iphoneos*]" = "iPhone Developer";
				COPY_PHASE_STRIP = NO;
				DEBUG_INFORMATION_FORMAT = dwarf;
				ENABLE_STRICT_OBJC_MSGSEND = YES;
				ENABLE_TESTABILITY = YES;
				GCC_C_LANGUAGE_STANDARD = gnu99;
				GCC_DYNAMIC_NO_PIC = NO;
				GCC_NO_COMMON_BLOCKS = YES;
				GCC_OPTIMIZATION_LEVEL = 0;
				GCC_PREPROCESSOR_DEFINITIONS = (
					"DEBUG=1",
					"${'$'}(inherited)",
				);
				GCC_WARN_64_TO_32_BIT_CONVERSION = YES;
				GCC_WARN_ABOUT_RETURN_TYPE = YES_ERROR;
				GCC_WARN_UNDECLARED_SELECTOR = YES;
				GCC_WARN_UNINITIALIZED_AUTOS = YES_AGGRESSIVE;
				GCC_WARN_UNUSED_FUNCTION = YES;
				GCC_WARN_UNUSED_VARIABLE = YES;
				IPHONEOS_DEPLOYMENT_TARGET = 9.3;
				MTL_ENABLE_DEBUG_INFO = YES;
				ONLY_ACTIVE_ARCH = YES;
				SDKROOT = iphoneos;
				TARGETED_DEVICE_FAMILY = "1,2";
			};
			name = Debug;
		};
		58C6F53A1DA66CB600309CB6 /* Release */ = {
			isa = XCBuildConfiguration;
			buildSettings = {
				ALWAYS_SEARCH_USER_PATHS = NO;
				CLANG_ANALYZER_NONNULL = YES;
				CLANG_CXX_LANGUAGE_STANDARD = "gnu++0x";
				CLANG_CXX_LIBRARY = "libc++";
				CLANG_ENABLE_MODULES = YES;
				CLANG_ENABLE_OBJC_ARC = YES;
				CLANG_WARN_BOOL_CONVERSION = YES;
				CLANG_WARN_CONSTANT_CONVERSION = YES;
				CLANG_WARN_DIRECT_OBJC_ISA_USAGE = YES_ERROR;
				CLANG_WARN_EMPTY_BODY = YES;
				CLANG_WARN_ENUM_CONVERSION = YES;
				CLANG_WARN_INFINITE_RECURSION = YES;
				CLANG_WARN_INT_CONVERSION = YES;
				CLANG_WARN_OBJC_ROOT_CLASS = YES_ERROR;
				CLANG_WARN_SUSPICIOUS_MOVE = YES;
				CLANG_WARN_UNREACHABLE_CODE = YES;
				CLANG_WARN__DUPLICATE_METHOD_MATCH = YES;
				"CODE_SIGN_IDENTITY[sdk=iphoneos*]" = "iPhone Developer";
				COPY_PHASE_STRIP = NO;
				DEBUG_INFORMATION_FORMAT = "dwarf-with-dsym";
				ENABLE_NS_ASSERTIONS = NO;
				ENABLE_STRICT_OBJC_MSGSEND = YES;
				GCC_C_LANGUAGE_STANDARD = gnu99;
				GCC_NO_COMMON_BLOCKS = YES;
				GCC_WARN_64_TO_32_BIT_CONVERSION = YES;
				GCC_WARN_ABOUT_RETURN_TYPE = YES_ERROR;
				GCC_WARN_UNDECLARED_SELECTOR = YES;
				GCC_WARN_UNINITIALIZED_AUTOS = YES_AGGRESSIVE;
				GCC_WARN_UNUSED_FUNCTION = YES;
				GCC_WARN_UNUSED_VARIABLE = YES;
				IPHONEOS_DEPLOYMENT_TARGET = 9.3;
				MTL_ENABLE_DEBUG_INFO = NO;
				SDKROOT = iphoneos;
				TARGETED_DEVICE_FAMILY = "1,2";
				VALIDATE_PRODUCT = YES;
			};
			name = Release;
		};
		58C6F53C1DA66CB600309CB6 /* Debug */ = {
			isa = XCBuildConfiguration;
			baseConfigurationReference = 5817739D1E37A10D004E28A9 /* custom.xcconfig */;
			buildSettings = {
				DEAD_CODE_STRIPPING = NO;
				ENABLE_BITCODE = NO;
				FRAMEWORK_SEARCH_PATHS = (
					"${'$'}(inherited)",
					"${'$'}{MOE_FRAMEWORK_PATH}",
				);
				INFOPLIST_FILE = "ios-moe/Info.plist";
				LD_RUNPATH_SEARCH_PATHS = "${'$'}(inherited) @executable_path/Frameworks";
				MOE_COPY_ANDROID_CACERTS = NO;
				MOE_FRAMEWORK_PATH = "${'$'}{MOE_SDK_PATH}/sdk/${'$'}{PLATFORM_NAME}";
				MOE_OTHER_LDFLAGS = "${'$'}{MOE_SECT_OAT} ${'$'}{MOE_SECT_ART} ${'$'}{MOE_SEGPROT} ${'$'}{MOE_PAGEZERO} ${'$'}{MOE_CUSTOM_OTHER_LDFLAGS} -lstdc++ -framework MOE";
				"MOE_PAGEZERO[sdk=iphoneos*]" = "";
				"MOE_PAGEZERO[sdk=iphonesimulator*]" = "-pagezero_size 4096";
				MOE_PROJECT_BUILD_DIR = "${'$'}{MOE_PROJECT_DIR}/build";
				MOE_PROJECT_DIR = "${'$'}{SRCROOT}/../";
				MOE_SDK_PATH = "${'$'}{MOE_PROJECT_BUILD_DIR}/moe/sdk";
				MOE_SECT_ART = "-sectcreate __ARTDATA __artdata \"${'$'}{MOE_PROJECT_BUILD_DIR}/moe/main/xcode/${'$'}{CONFIGURATION}${'$'}{EFFECTIVE_PLATFORM_NAME}/${'$'}{arch}.art\"";
				MOE_SECT_OAT = "-sectcreate __OATDATA __oatdata \"${'$'}{MOE_PROJECT_BUILD_DIR}/moe/main/xcode/${'$'}{CONFIGURATION}${'$'}{EFFECTIVE_PLATFORM_NAME}/${'$'}{arch}.oat\"";
				"MOE_SEGPROT[sdk=iphoneos*]" = "-segprot __OATDATA rx rx -segprot __ARTDATA rw rw";
				"MOE_SEGPROT[sdk=iphonesimulator*]" = "-segprot __OATDATA rwx rx -segprot __ARTDATA rwx rw";
				ONLY_ACTIVE_ARCH = YES;
				OTHER_LDFLAGS = (
					"${'$'}(inherited)",
					"${'$'}{MOE_OTHER_LDFLAGS}",
					"${'$'}(LIBGDX_NATIVES)",
				);
				PRODUCT_BUNDLE_IDENTIFIER = "${project.basic.rootPackage}";
				PRODUCT_NAME = "${project.basic.name}";
				STRIP_STYLE = "non-global";
			};
			name = Debug;
		};
		58C6F53D1DA66CB600309CB6 /* Release */ = {
			isa = XCBuildConfiguration;
			baseConfigurationReference = 5817739D1E37A10D004E28A9 /* custom.xcconfig */;
			buildSettings = {
				DEAD_CODE_STRIPPING = NO;
				ENABLE_BITCODE = NO;
				FRAMEWORK_SEARCH_PATHS = (
					"${'$'}(inherited)",
					"${'$'}{MOE_FRAMEWORK_PATH}",
				);
				INFOPLIST_FILE = "ios-moe/Info.plist";
				LD_RUNPATH_SEARCH_PATHS = "${'$'}(inherited) @executable_path/Frameworks";
				MOE_COPY_ANDROID_CACERTS = NO;
				MOE_FRAMEWORK_PATH = "${'$'}{MOE_SDK_PATH}/sdk/${'$'}{PLATFORM_NAME}";
				MOE_OTHER_LDFLAGS = "${'$'}{MOE_SECT_OAT} ${'$'}{MOE_SECT_ART} ${'$'}{MOE_SEGPROT} ${'$'}{MOE_PAGEZERO} ${'$'}{MOE_CUSTOM_OTHER_LDFLAGS} -lstdc++ -framework MOE";
				"MOE_PAGEZERO[sdk=iphoneos*]" = "";
				"MOE_PAGEZERO[sdk=iphonesimulator*]" = "-pagezero_size 4096";
				MOE_PROJECT_BUILD_DIR = "${'$'}{MOE_PROJECT_DIR}/build";
				MOE_PROJECT_DIR = "${'$'}{SRCROOT}/../";
				MOE_SDK_PATH = "${'$'}{MOE_PROJECT_BUILD_DIR}/moe/sdk";
				MOE_SECT_ART = "-sectcreate __ARTDATA __artdata \"${'$'}{MOE_PROJECT_BUILD_DIR}/moe/main/xcode/${'$'}{CONFIGURATION}${'$'}{EFFECTIVE_PLATFORM_NAME}/${'$'}{arch}.art\"";
				MOE_SECT_OAT = "-sectcreate __OATDATA __oatdata \"${'$'}{MOE_PROJECT_BUILD_DIR}/moe/main/xcode/${'$'}{CONFIGURATION}${'$'}{EFFECTIVE_PLATFORM_NAME}/${'$'}{arch}.oat\"";
				"MOE_SEGPROT[sdk=iphoneos*]" = "-segprot __OATDATA rx rx -segprot __ARTDATA rw rw";
				"MOE_SEGPROT[sdk=iphonesimulator*]" = "-segprot __OATDATA rwx rx -segprot __ARTDATA rwx rw";
				ONLY_ACTIVE_ARCH = NO;
				OTHER_LDFLAGS = (
					"${'$'}(inherited)",
					"${'$'}{MOE_OTHER_LDFLAGS}",
					"${'$'}(LIBGDX_NATIVES)",
				);
				PRODUCT_BUNDLE_IDENTIFIER = "${project.basic.rootPackage}";
				PRODUCT_NAME = "${project.basic.name}";
				STRIP_STYLE = "non-global";
			};
			name = Release;
		};
/* End XCBuildConfiguration section */

/* Begin XCConfigurationList section */
		5863B88F1DA682C800E46B6A /* Build configuration list for PBXNativeTarget "ios-moe-Test" */ = {
			isa = XCConfigurationList;
			buildConfigurations = (
				5863B8901DA682C800E46B6A /* Debug */,
				5863B8911DA682C800E46B6A /* Release */,
			);
			defaultConfigurationIsVisible = 0;
			defaultConfigurationName = Release;
		};
		58C6F51F1DA66CB600309CB6 /* Build configuration list for PBXProject "ios-moe" */ = {
			isa = XCConfigurationList;
			buildConfigurations = (
				58C6F5391DA66CB600309CB6 /* Debug */,
				58C6F53A1DA66CB600309CB6 /* Release */,
			);
			defaultConfigurationIsVisible = 0;
			defaultConfigurationName = Release;
		};
		58C6F53B1DA66CB600309CB6 /* Build configuration list for PBXNativeTarget "ios-moe" */ = {
			isa = XCConfigurationList;
			buildConfigurations = (
				58C6F53C1DA66CB600309CB6 /* Debug */,
				58C6F53D1DA66CB600309CB6 /* Release */,
			);
			defaultConfigurationIsVisible = 0;
			defaultConfigurationName = Release;
		};
/* End XCConfigurationList section */
	};
	rootObject = 58C6F51C1DA66CB600309CB6 /* Project object */;
}
""")
