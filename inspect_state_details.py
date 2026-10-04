import zipfile, glob, os, subprocess

jar = "/home/viper/.gradle/caches/fabric-loom/26.3/minecraft-client.jar"

classes = [
    "net.minecraft.client.renderer.state.level.FirstPersonHandsAndItemsRenderState",
    "net.minecraft.client.renderer.entity.ItemRenderer"
]

for cls in classes:
    print(f"=== {cls} ===")
    res = subprocess.run(["javap", "-p", "-cp", jar, cls], capture_output=True, text=True)
    if res.returncode == 0:
        print(res.stdout)
    else:
        print(f"Could not inspect {cls}")

