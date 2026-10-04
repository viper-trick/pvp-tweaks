import zipfile, glob, os, subprocess

pattern = os.path.expanduser("~/.gradle/**/*.jar")
jars = glob.glob(pattern, recursive=True)

found_jar = None
for jar in jars:
    if "minecraft" in jar.lower() or "loom" in jar.lower():
        try:
            with zipfile.ZipFile(jar, 'r') as z:
                for name in z.namelist():
                    if "FirstPersonHandsAndItemsRenderer.class" in name:
                        found_jar = jar
                        class_path = name.replace("/", ".").replace(".class", "")
                        print(f"Found class in JAR: {jar}")
                        print(f"Class path: {class_path}")
                        
                        res = subprocess.run(["javap", "-p", "-cp", jar, class_path], capture_output=True, text=True)
                        print("\n=== CLASS MEMBERS ===")
                        print(res.stdout)
                        break
        except Exception:
            continue
    if found_jar:
        break

if not found_jar:
    print("Could not find FirstPersonHandsAndItemsRenderer.class in gradle cache.")
