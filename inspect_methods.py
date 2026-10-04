import glob, os, subprocess

loom_jars = glob.glob(os.path.expanduser("~/.gradle/caches/fabric-loom/**/minecraft-*.jar"), recursive=True) + \
            glob.glob(os.path.expanduser("~/.gradle/caches/**/minecraft*.jar"), recursive=True)

for jar in loom_jars:
    if "26.3" in jar or "26_3" in jar or "1.21" in jar:
        print("=== FirstPersonHandsAndItemsRenderer All Methods ===")
        res1 = subprocess.run(["javap", "-p", "-cp", jar, "net.minecraft.client.renderer.FirstPersonHandsAndItemsRenderer"], capture_output=True, text=True)
        if res1.returncode == 0:
            print(res1.stdout)

        print("=== FirstPersonHandsAndItemsRenderState Fields ===")
        res2 = subprocess.run(["javap", "-p", "-cp", jar, "net.minecraft.client.renderer.state.level.FirstPersonHandsAndItemsRenderState"], capture_output=True, text=True)
        if res2.returncode == 0:
            print(res2.stdout)

        break
