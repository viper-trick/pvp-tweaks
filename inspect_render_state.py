import glob, os, subprocess

loom_jars = glob.glob(os.path.expanduser("~/.gradle/caches/fabric-loom/**/minecraft-*.jar"), recursive=True) + \
            glob.glob(os.path.expanduser("~/.gradle/caches/**/minecraft*.jar"), recursive=True)

for jar in loom_jars:
    if "26.3" in jar or "26_3" in jar or "1.21" in jar:
        print("=== All Fields of FirstPersonHandsAndItemsRenderer ===")
        res1 = subprocess.run(["javap", "-private", "-cp", jar, "net.minecraft.client.renderer.FirstPersonHandsAndItemsRenderer"], capture_output=True, text=True)
        if res1.returncode == 0:
            for line in res1.stdout.splitlines():
                if "field" in line or (" " in line and not line.strip().startswith("public") and "(" not in line and "class" not in line):
                    print(line)

        print("\n=== FirstPersonHandsAndItemsRenderState Fields ===")
        res2 = subprocess.run(["javap", "-private", "-cp", jar, "net.minecraft.client.renderer.state.level.FirstPersonHandsAndItemsRenderState"], capture_output=True, text=True)
        if res2.returncode == 0:
            print(res2.stdout)

        break
