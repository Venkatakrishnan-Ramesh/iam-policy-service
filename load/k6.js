import http from 'k6/http';
import { check } from 'k6';
export const options={stages:[{duration:'30s',target:20},{duration:'2m',target:20},{duration:'30s',target:0}],thresholds:{http_req_failed:['rate<0.01'],http_req_duration:['p(95)<250']}};
const token=__ENV.ACCESS_TOKEN;
export default function(){
 const body=JSON.stringify({subject:`user-${__VU}`,roles:['engineer'],action:'read',resource:'document/42',attributes:{department:'platform'}});
 const r=http.post(`${__ENV.BASE_URL||'http://localhost:8080'}/api/v1/decisions`,body,{headers:{Authorization:`Bearer ${token}`,'Content-Type':'application/json'}});
 check(r,{'status 200':x=>x.status===200,'has decision':x=>x.json('allowed')!==undefined});
}
