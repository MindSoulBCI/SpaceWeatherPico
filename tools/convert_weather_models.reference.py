import bpy,sys,json
from pathlib import Path
root=Path('/Users/apple/Documents/ChatGPT/jinyi天气')
bpy.ops.wm.open_mainfile(filepath=str(root/'.scratch/weather-upstream/weather/Dynamic-Weather/Assets/Models/tornadoMesh.blend'))
print('MESHES',[(o.name,len(o.data.vertices),list(o.dimensions)) for o in bpy.data.objects if o.type=='MESH'])
for o in list(bpy.data.objects):
 if o.type!='MESH':bpy.data.objects.remove(o,do_unlink=True)
for o in bpy.context.scene.objects:
 o.select_set(True)
 o.data.materials.clear()
 mat=bpy.data.materials.new('TornadoCloud');mat.diffuse_color=(.36,.43,.52,1);mat.use_nodes=True
 bs=mat.node_tree.nodes.get('Principled BSDF');bs.inputs['Base Color'].default_value=(.36,.43,.52,1);bs.inputs['Roughness'].default_value=.95
 o.data.materials.append(mat)
bpy.ops.export_scene.gltf(filepath=str(root/'SpatialWeatherMindSoul/app/src/main/assets/weather/tornado.glb'),export_format='GLB',use_selection=True)
bpy.ops.wm.read_factory_settings(use_empty=True)
bpy.ops.wm.obj_import(filepath=str(root/'.scratch/weather-upstream/sky/Assets/DMSkybox/SkysphereMesh.obj'))
print('SKY',[(o.name,len(o.data.vertices),list(o.dimensions)) for o in bpy.context.scene.objects if o.type=='MESH'])
bpy.ops.export_scene.gltf(filepath=str(root/'SpatialWeatherMindSoul/app/src/main/assets/weather/source_skysphere.glb'),export_format='GLB')
