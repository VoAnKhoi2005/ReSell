import { mount } from 'svelte'
import './app.css'
import 'bootstrap/dist/css/bootstrap.min.css';
import App from './App.svelte'

const app = mount(App, {
  target: document.getElementById('app'),
})

export default app
