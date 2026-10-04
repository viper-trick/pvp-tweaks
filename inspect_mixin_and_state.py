import glob, os, subprocess

loom_jars = glob.glob(os.path.expanduser("~/.gradle/caches/fabric-loom/**/minecraft-*.jar"), recursive=True) + \
            glob.glob(os.path.expanduser("~/.gradle/caches/**/minecraft*.jar"), recursive=True)

for jar in loom_jars:
    if "26.3" in jar or "26_3" in jar or "1.21" in jar:
        print("=== FirstPersonHandsAndItemsRenderer Fields ===")
        res1 = subprocess.run(["javap", "-p", "-cp", jar, "net.minecraft.client.renderer.FirstPersonHandsAndItemsRenderer"], capture_output=True, text=True)
        if res1.returncode == 0:
            for line in res1.stdout.splitlines():
                if "(" not in line and "class" not in line and "{" not in line and "}" not in line:
                    print(line)

        print("\n=== FirstPersonHandsAndItemsRenderState Class Structure ===")
        res2 = subprocess.run(["javap", "-p", "-cp", jar, "net.minecraft.client.renderer.state.level.FirstPersonHandsAndItemsRenderState"], capture_output=True, text=True)
        if res2.returncode == 0:
            print(res2.stdout)
        else:
            # אם השם של ה-RenderState שונה, נמצא את המיקום שלו
            res3 = subprocess.run(["jar", "-tf", jar], capture_output=True, text=True)
            for entry in res3.stdout.splitlines():
                if "FirstPerson" in entry and entry.endswith(".class"):
                    print("Found class:", entry.replace("/", ".").replace(".class", ""))
        break

print("\n=== Current ShieldRendererMixin.java Source ===")
for path in glob.glob("src/build-26.3/**/ShieldRendererMixin.java", recursive=True):
    with open(path, "r") as f:
        print(f.read())
