import { useState } from 'react'
import reactLogo from './assets/react.svg'
import viteLogo from './assets/vite.svg'
import heroImg from './assets/hero.png'
import './App.css'

import Sidebar from "./components/Sidebar"

function App() {
  return (
    <div className="flex min-h-screen bg-slate-50">

      <Sidebar />

      <main className="flex-1 p-8">

        <h1 className="text-3xl font-bold text-slate-800">
          Ana Sayfa
        </h1>

        <p className="mt-2 text-slate-500">
          Ürün analiz platformuna hoş geldiniz.
        </p>

      </main>

    </div>
  )
}

export default App

