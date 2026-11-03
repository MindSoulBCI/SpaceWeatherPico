"""PICO mesh adaptation of the reference tornado's spiral bands and wind streaks.
The original three upstream meshes are retained separately in tornado.glb.
This generates geometric ribbons, not a port of Unity Shader Graph.
"""
from pathlib import Path
import math,struct,json
root=Path(__file__).resolve().parents[1]/'app/src/main/assets/weather'
def save(name,strips,color):
 p=[];ids=[]
 for points in strips:
  start=len(p)//3
  for a,b in points:p.extend(a);p.extend(b)
  for i in range(len(points)-1):
   k=start+i*2;ids.extend([k,k+1,k+2,k+1,k+3,k+2])
 pb=struct.pack('<'+'f'*len(p),*p);ib=struct.pack('<'+'I'*len(ids),*ids);b=pb+ib
 d={'asset':{'version':'2.0'},'scene':0,'scenes':[{'nodes':[0]}],'nodes':[{'mesh':0}],'meshes':[{'primitives':[{'attributes':{'POSITION':0},'indices':1,'material':0}]}],'materials':[{'doubleSided':True,'pbrMetallicRoughness':{'baseColorFactor':color,'metallicFactor':0,'roughnessFactor':1},'extensions':{'KHR_materials_unlit':{}}}],'extensionsUsed':['KHR_materials_unlit'],'buffers':[{'byteLength':len(b)}],'bufferViews':[{'buffer':0,'byteLength':len(pb)},{'buffer':0,'byteOffset':len(pb),'byteLength':len(ib)}],'accessors':[{'bufferView':0,'componentType':5126,'count':len(p)//3,'type':'VEC3','min':[min(p[k::3]) for k in range(3)],'max':[max(p[k::3]) for k in range(3)]},{'bufferView':1,'componentType':5125,'count':len(ids),'type':'SCALAR'}]}
 s=json.dumps(d).encode();s+=b' '*(-len(s)%4)
 (root/name).write_bytes(struct.pack('<III',0x46546c67,2,28+len(s)+len(b))+struct.pack('<II',len(s),0x4e4f534a)+s+struct.pack('<II',len(b),0x004e4942)+b)
strips=[]
for band in range(22):
 points=[]
 for i in range(97):
  t=i/96;a=t*math.pi*(2.3 if band<14 else 1.5)+band*2.4
  h=(band/14*10+t*2) if band<14 else 10+(band-14)*.25
  radius=.18+(h/12)**1.9*4.7
  radius*=1+.08*math.sin(a*3+band)
  x=math.cos(a)*radius+.22*math.sin(h);z=math.sin(a)*radius
  w=(.055+h*.024)*math.sin(math.pi*t)**.5
  points.append(([x,h-w,z],[x,h+w,z]))
 strips.append(points)
save('tornado_ribbons.glb',strips,[.10,.12,.15,1])
strips=[]
for band in range(14):
 points=[]
 for i in range(97):
  t=i/96;a=t*math.pi*1.5+band*1.7;r=3.5+(band%4)*.6
  h=.8+(band%6)*.6+.1*math.sin(a*5+band);w=.009*math.sin(math.pi*t)
  points.append(([math.cos(a)*r,h-w,math.sin(a)*r],[math.cos(a)*r,h+w,math.sin(a)*r]))
 strips.append(points)
save('tornado_wind.glb',strips,[.72,.79,.83,1])
