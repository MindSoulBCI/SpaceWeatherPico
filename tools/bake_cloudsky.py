"""CPU bake adapted from keijiro/CloudSkybox SampleNoise and Beer integration (MIT).
Uses the original Alpha8 Perlin/Worley textures; portable unlit GLB canopy.
Baked lighting and rotating layers replace the original real-time Unity raymarch.
Run with Blender's bundled Python/numpy: blender -b --python tools/bake_cloudsky.py.
"""
import numpy as np,math,json,struct,zlib
from pathlib import Path
root=Path(__file__).resolve().parents[1];out=root/'app/src/main/assets/weather'
vol=[]
for n in ['Worley','Perlin']:
 raw=np.frombuffer((root/f'third_party/CloudSkybox/{n}.alpha8').read_bytes(),dtype=np.uint8);side=round(len(raw)**(1/3));vol.append(raw.reshape(side,side,side).astype(np.float32)/255)
def sample(v,p):
 side=len(v);q=(p%1)*side;i=np.floor(q).astype(np.int32);f=q-i;r=np.zeros(p.shape[:-1],np.float32)
 for a in range(2):
  for b in range(2):
   for c in range(2):r+=v[(i[...,2]+c)%side,(i[...,1]+b)%side,(i[...,0]+a)%side]*(f[...,0] if a else 1-f[...,0])*(f[...,1] if b else 1-f[...,1])*(f[...,2] if c else 1-f[...,2])
 return r
def smooth(x):
 x=np.clip(x,0,1);return x*x*(3-2*x)
def density(p,bias):
 n=np.clip(-8.5*sample(vol[0],p*1.34e-5)+2.49*sample(vol[1],p*13.57e-5)+bias,0,1)
 return n*smooth((p[...,1]-1000)/400)*smooth((5000-p[...,1])/1600)
def png(pixels):
 h,w,_=pixels.shape
 def chunk(t,b):return struct.pack('>I',len(b))+t+b+struct.pack('>I',zlib.crc32(t+b)&0xffffffff)
 raw=b''.join(b'\x00'+row.tobytes() for row in pixels)
 return b'\x89PNG\r\n\x1a\n'+chunk(b'IHDR',struct.pack('>IIBBBBB',w,h,8,6,0,0,0))+chunk(b'IDAT',zlib.compress(raw))+chunk(b'IEND',b'')
w,h=512,256
az=np.linspace(0,2*np.pi,w)[None,:];theta=np.linspace(.001,1.4,h)[:,None]
ray=np.stack(np.broadcast_arrays(np.sin(theta)*np.cos(az),np.cos(theta),np.sin(theta)*np.sin(az)),axis=-1)
for name,bias in [('day',2.05),('overcast',2.8)]:
 depth=np.zeros((h,w));stride=4000/ray[...,1]/32
 for s in range(32):
  p=ray*((1000+(s+.5)*4000/32)/ray[...,1])[...,None]
  depth+=density(p,bias)*stride
 opacity=(1-np.exp(-.0025*depth))*smooth((1.4-theta)/.16)*(.82 if name=='day' else .91)
 light=.62+.38*np.exp(-depth*.0006)
 tint=np.array([.97,.98,1.0] if name=='day' else [.75,.80,.87])
 rgb=light[...,None]*tint
 pixels=(np.clip(np.concatenate([rgb,opacity[...,None]],axis=-1),0,1)*255).astype(np.uint8)
 texture=png(pixels);(out/f'cloudsky_{name}.png').write_bytes(texture)
 positions=[];uv=[];indices=[]
 for j in range(33):
  t=.001+1.399*j/32
  for i in range(97):
   a=2*math.pi*i/96;positions.extend([12*math.sin(t)*math.cos(a),12*math.cos(t),12*math.sin(t)*math.sin(a)]);uv.extend([i/96,j/32])
 for j in range(32):
  for i in range(96):
   k=j*97+i;indices.extend([k,k+97,k+1,k+1,k+97,k+98])
 chunks=[];views=[]
 def add(b):
  views.append({'buffer':0,'byteOffset':sum(map(len,chunks)),'byteLength':len(b)});chunks.append(b+b'\0'*(-len(b)%4));return len(views)-1
 pv=add(struct.pack('<'+'f'*len(positions),*positions));tv=add(struct.pack('<'+'f'*len(uv),*uv));iv=add(struct.pack('<'+'H'*len(indices),*indices));im=add(texture)
 d={'asset':{'version':'2.0'},'scene':0,'scenes':[{'nodes':[0]}],'nodes':[{'mesh':0}],'meshes':[{'primitives':[{'attributes':{'POSITION':0,'TEXCOORD_0':1},'indices':2,'material':0}]}],'materials':[{'doubleSided':True,'alphaMode':'BLEND','pbrMetallicRoughness':{'baseColorTexture':{'index':0},'metallicFactor':0,'roughnessFactor':1},'extensions':{'KHR_materials_unlit':{}}}],'extensionsUsed':['KHR_materials_unlit'],'textures':[{'source':0}],'images':[{'bufferView':im,'mimeType':'image/png'}],'bufferViews':views,'accessors':[{'bufferView':pv,'componentType':5126,'count':len(positions)//3,'type':'VEC3','min':[min(positions[k::3]) for k in range(3)],'max':[max(positions[k::3]) for k in range(3)]},{'bufferView':tv,'componentType':5126,'count':len(uv)//2,'type':'VEC2'},{'bufferView':iv,'componentType':5123,'count':len(indices),'type':'SCALAR'}],'buffers':[{'byteLength':sum(map(len,chunks))}]}
 b=b''.join(chunks);js=json.dumps(d).encode();js+=b' '*(-len(js)%4)
 (out/f'cloudsky_{name}.glb').write_bytes(struct.pack('<III',0x46546c67,2,28+len(js)+len(b))+struct.pack('<II',len(js),0x4e4f534a)+js+struct.pack('<II',len(b),0x004e4942)+b)
 print(name,'mean opacity',float(opacity.mean()))
