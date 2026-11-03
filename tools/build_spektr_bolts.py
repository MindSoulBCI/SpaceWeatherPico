"""SpektrLightning CPU adaptation. Copyright (C) 2015 Keijiro Takahashi, MIT.
Simplex noise: Copyright (C) 2011 Ashima Arts, MIT. Original sources and notices
are retained in third_party/SpektrLightning. Four time samples, cross ribbons
and a translucent halo replace Unity's shader and HDR bloom on PICO.
"""
from pathlib import Path
import math,json,struct
root=Path(__file__).resolve().parents[1]/'app/src/main/assets/weather'
def noise(x,y):
 c=.211324865405187;d=.366025403784439
 ix=math.floor(x+(x+y)*d);iy=math.floor(y+(x+y)*d)
 x0=x-ix+(ix+iy)*c;y0=y-iy+(ix+iy)*c
 a,b=(1,0) if x0>=y0 else (0,1)
 corners=[(x0,y0,0,0),(x0+c-a,y0+c-b,a,b),(x0-.577350269189626,y0-.577350269189626,1,1)]
 def perm(z):return ((z*34+1)*z)%289
 result=0
 for px,py,dx,dy in corners:
  p=perm(perm((iy%289)+dy)+(ix%289)+dx)
  m=max(.5-px*px-py*py,0)**4
  q=2*((p/41)%1)-1;h=abs(q)-.5;g=q-math.floor(q+.5)
  result+=m*(1.79284291400159-.85373472095314*(g*g+h*h))*(g*px+h*py)
 return result*130
for frame in range(4):
 positions=[];indices=[[],[]];t=frame*.075
 for strand in range(3):
  points=[]
  for i in range(81):
   u=i/80;p=u*3.6;seed=(strand+frame*.13)*131.1
   def displace(offset):return .20*noise(1.4*p+offset,t*.1)+.020*noise(14*p+offset,t)
   x=displace(seed*13.45)+(strand-1)*.08;z=displace(seed*-21.73)
   points.append((x,3.6*(1-u),z))
  for mat,width in [(0,.018),(1,.07)]:
   for axis in [0,2]:
    start=len(positions)//3
    for x,y,z in points:
     for side in [-1,1]:positions.extend([x+(width*side if axis==0 else 0),y,z+(width*side if axis==2 else 0)])
    for i in range(80):
     k=start+i*2;indices[mat].extend([k,k+1,k+2,k+1,k+3,k+2])
 chunks=[];views=[]
 def add(b):
  views.append({'buffer':0,'byteOffset':sum(map(len,chunks)),'byteLength':len(b)});chunks.append(b+b'\0'*(-len(b)%4));return len(views)-1
 pv=add(struct.pack('<'+'f'*len(positions),*positions));ivs=[add(struct.pack('<'+'H'*len(a),*a)) for a in indices]
 d={'asset':{'version':'2.0'},'scene':0,'scenes':[{'nodes':[0]}],'nodes':[{'mesh':0}],'meshes':[{'primitives':[{'attributes':{'POSITION':0},'indices':i+1,'material':i} for i in range(2)]}],'materials':[{'doubleSided':True,'alphaMode':'BLEND' if i else 'OPAQUE','pbrMetallicRoughness':{'baseColorFactor':([.94,.98,1,1] if i==0 else [.3,.55,1,.14]),'metallicFactor':0,'roughnessFactor':1},'extensions':{'KHR_materials_unlit':{}}} for i in range(2)],'extensionsUsed':['KHR_materials_unlit'],'bufferViews':views,'accessors':[{'bufferView':pv,'componentType':5126,'count':len(positions)//3,'type':'VEC3','min':[min(positions[k::3]) for k in range(3)],'max':[max(positions[k::3]) for k in range(3)]}]+[{'bufferView':iv,'componentType':5123,'count':len(a),'type':'SCALAR'} for iv,a in zip(ivs,indices)],'buffers':[{'byteLength':sum(map(len,chunks))}]}
 b=b''.join(chunks);js=json.dumps(d).encode();js+=b' '*(-len(js)%4)
 (root/f'spektr_bolt_{frame}.glb').write_bytes(struct.pack('<III',0x46546c67,2,28+len(js)+len(b))+struct.pack('<II',len(js),0x4e4f534a)+js+struct.pack('<II',len(b),0x004e4942)+b)
