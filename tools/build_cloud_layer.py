"""Build an inward textured cloud canopy using unmodified DMSkybox MIT texture."""
import math,struct,json
from pathlib import Path
root=Path(__file__).resolve().parents[1]
p=[];uv=[];indices=[]
for j in range(13):
 theta=.08+(math.pi*.42)*j/12
 for i in range(65):
  a=2*math.pi*i/64;p.extend([12*math.sin(theta)*math.cos(a),12*math.cos(theta),12*math.sin(theta)*math.sin(a)]);uv.extend([i/64*3,j/12])
for j in range(12):
 for i in range(64):
  a=j*65+i;indices.extend([a,a+65,a+1,a+1,a+65,a+66])
chunks=[];views=[]
def add(data):
 offset=sum(map(len,chunks));views.append({'buffer':0,'byteOffset':offset,'byteLength':len(data)});chunks.append(data+b'\0'*((-len(data))%4));return len(views)-1
pv=add(struct.pack('<'+'f'*len(p),*p));tv=add(struct.pack('<'+'f'*len(uv),*uv));iv=add(struct.pack('<'+'H'*len(indices),*indices));image=add((root/'app/src/main/assets/weather/cloud_noise.png').read_bytes())
d={'asset':{'version':'2.0'},'scene':0,'scenes':[{'nodes':[0]}],'nodes':[{'mesh':0}],'meshes':[{'primitives':[{'attributes':{'POSITION':0,'TEXCOORD_0':1},'indices':2,'material':0}]}],'materials':[{'pbrMetallicRoughness':{'baseColorTexture':{'index':0},'baseColorFactor':[.7,.8,1,.2],'metallicFactor':0,'roughnessFactor':1},'alphaMode':'BLEND','doubleSided':True,'extensions':{'KHR_materials_unlit':{}}}],'extensionsUsed':['KHR_materials_unlit'],'textures':[{'source':0}],'images':[{'bufferView':image,'mimeType':'image/png'}],'bufferViews':views,'accessors':[{'bufferView':pv,'componentType':5126,'count':len(p)//3,'type':'VEC3','min':[min(p[k::3]) for k in range(3)],'max':[max(p[k::3]) for k in range(3)]},{'bufferView':tv,'componentType':5126,'count':len(uv)//2,'type':'VEC2'},{'bufferView':iv,'componentType':5123,'count':len(indices),'type':'SCALAR'}],'buffers':[{'byteLength':sum(map(len,chunks))}]}
b=b''.join(chunks);s=json.dumps(d).encode();s+=b' '*((-len(s))%4)
(root/'app/src/main/assets/weather/cloud_layer.glb').write_bytes(struct.pack('<III',0x46546c67,2,28+len(s)+len(b))+struct.pack('<II',len(s),0x4e4f534a)+s+struct.pack('<II',len(b),0x004e4942)+b)
# Local evaluation adaptation of user-requested rain texture; see third-party notices.
chunks=[];views=[]
p=[-3,0,0,3,0,0,-3,6,0,3,6,0];uv=[0,1,1,1,0,0,1,0];indices=[0,1,2,2,1,3]
pv=add(struct.pack('<12f',*p));tv=add(struct.pack('<8f',*uv));iv=add(struct.pack('<6H',*indices));im=add((root/'app/src/main/assets/weather/rain.png').read_bytes())
d['bufferViews']=views;d['accessors']=[{'bufferView':pv,'componentType':5126,'count':4,'type':'VEC3','min':[-3,0,0],'max':[3,6,0]},{'bufferView':tv,'componentType':5126,'count':4,'type':'VEC2'},{'bufferView':iv,'componentType':5123,'count':6,'type':'SCALAR'}];d['images'][0]['bufferView']=im;d['materials'][0]['pbrMetallicRoughness']['baseColorFactor']=[.6,.8,1,.16];d['buffers']=[{'byteLength':sum(map(len,chunks))}]
b=b''.join(chunks);s=json.dumps(d).encode();s+=b' '*((-len(s))%4)
(root/'app/src/main/assets/weather/rain_layer.glb').write_bytes(struct.pack('<III',0x46546c67,2,28+len(s)+len(b))+struct.pack('<II',len(s),0x4e4f534a)+s+struct.pack('<II',len(b),0x004e4942)+b)
