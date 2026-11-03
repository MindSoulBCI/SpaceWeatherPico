"""Convert original StylizedTrees LOD1 meshes to meter-scale parts for native wind animation."""
import bpy,json
from mathutils import Vector,Matrix
from pathlib import Path
root=Path(__file__).resolve().parents[1];source=root/'third_party/StylizedTrees'
out=root/'app/src/main/assets/weather/tree';out.mkdir(exist_ok=True)
bpy.ops.wm.read_factory_settings(use_empty=True)
bpy.ops.import_scene.fbx(filepath=str(source/'Tree_01_LOD1.fbx'))
objects=[o for o in bpy.context.scene.objects if o.type=='MESH']
points=[o.matrix_world@v.co for o in objects for v in o.data.vertices]
lo=Vector([min(v[k] for v in points) for k in range(3)]);hi=Vector([max(v[k] for v in points) for k in range(3)])
print('WORLD',lo[:],hi[:]);factor=2.6/(hi.z-lo.z);base=Vector(((lo.x+hi.x)/2,(lo.y+hi.y)/2,lo.z))
meta=[]
for number,o in enumerate(objects):
 matrix=o.matrix_world.copy();coords=[(matrix@v.co-base)*factor for v in o.data.vertices]
 leaf='Leaf' in o.name;center=sum(coords,Vector())/len(coords) if leaf else Vector()
 o.parent=None;o.matrix_world=Matrix.Identity(4)
 for v,p in zip(o.data.vertices,coords):v.co=p-center
 o.data.materials.clear()
 mat=bpy.data.materials.new(o.name+'Native');mat.use_nodes=True
 nodes=mat.node_tree.nodes;nodes.clear();output=nodes.new('ShaderNodeOutputMaterial');emission=nodes.new('ShaderNodeEmission')
 shade=.08*(number%3)
 emission.inputs['Color'].default_value=(.11+shade,.40+shade,.13+shade*.4,1) if leaf else (.28,.16,.08,1)
 mat.node_tree.links.new(emission.outputs[0],output.inputs['Surface']);o.data.materials.append(mat)
 bpy.ops.object.select_all(action='DESELECT');o.select_set(True);bpy.context.view_layer.objects.active=o
 name=('leaf_'+str(len(meta)) if leaf else 'trunk')+'.glb'
 bpy.ops.export_scene.gltf(filepath=str(out/name),export_format='GLB',use_selection=True)
 meta.append({'file':'weather/tree/'+name,'leaf':leaf,'position':[center.x,center.z,-center.y],'source':o.name,'vertices':len(o.data.vertices)})
(out/'parts.json').write_text(json.dumps(meta,indent=2));print('PARTS',meta)
