import os
import os.path
import shutil

if __name__ == "__main__":
    baseDir = r"F:\WorkSpace\expert"
    old_jars = os.listdir(os.path.join(baseDir,'jars'))
    print("Removing old jars...")
    for jar in old_jars:
        os.remove(os.path.join(baseDir,'jars',jar))
    modules = [
        "eureka",
        "config",
        "oauth2",
        "gateway",
        "account",
        "applicationform",
        "docs"
    ]
    for module in modules:
        jar = "%s-0.0.1-SNAPSHOT.jar" % module
        source = os.path.join(baseDir,module,"target",jar)
        target = os.path.join(baseDir,"jars",jar)
        print("Copying < %s > to < %s >..." % (source,target))
        shutil.copyfile(source,target)