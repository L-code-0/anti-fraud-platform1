/**
 * 设备检测工具类
 * 用于检测设备是否支持WebXR API，以及获取设备的相关信息
 */
export class DeviceDetector {
  /**
   * 检测设备是否支持WebXR API
   * @returns 是否支持WebXR API
   */
  static async isWebXRSupported(): Promise<boolean> {
    try {
      // 检查navigator.xr是否存在
      if (!navigator.xr) {
        console.log('WebXR API not supported');
        return false;
      }

      // 检查是否支持沉浸式VR会话
      const isImmersiveVRSupported = await navigator.xr.isSessionSupported('immersive-vr');
      console.log('Immersive VR supported:', isImmersiveVRSupported);

      // 检查是否支持沉浸式AR会话
      const isImmersiveARSupported = await navigator.xr.isSessionSupported('immersive-ar');
      console.log('Immersive AR supported:', isImmersiveARSupported);

      // 检查是否支持内联会话
      const isInlineSupported = await navigator.xr.isSessionSupported('inline');
      console.log('Inline supported:', isInlineSupported);

      // 只要支持其中一种会话类型，就认为支持WebXR
      return isImmersiveVRSupported || isImmersiveARSupported || isInlineSupported;
    } catch (error) {
      console.error('Error checking WebXR support:', error);
      return false;
    }
  }

  /**
   * 获取设备信息
   * @returns 设备信息对象
   */
  static getDeviceInfo(): Record<string, any> {
    const deviceInfo = {
      // 浏览器信息
      browser: {
        name: this.getBrowserName(),
        version: this.getBrowserVersion(),
      },
      // 操作系统信息
      os: {
        name: this.getOSName(),
        version: this.getOSVersion(),
      },
      // 设备信息
      device: {
        type: this.getDeviceType(),
        model: this.getDeviceModel(),
        screen: {
          width: window.screen.width,
          height: window.screen.height,
          pixelRatio: window.devicePixelRatio,
        },
      },
      // WebXR支持情况
      webxr: {
        supported: false, // 将在后续检测
        sessions: {
          'immersive-vr': false,
          'immersive-ar': false,
          'inline': false,
        },
      },
    };

    return deviceInfo;
  }

  /**
   * 检测设备的WebXR支持情况并返回完整的设备信息
   * @returns 包含WebXR支持情况的设备信息对象
   */
  static async getDeviceInfoWithWebXRSupport(): Promise<Record<string, any>> {
    const deviceInfo = this.getDeviceInfo();

    try {
      // 检测WebXR支持情况
      if (navigator.xr) {
        deviceInfo.webxr.supported = true;

        // 检测各种会话类型的支持情况
        deviceInfo.webxr.sessions['immersive-vr'] = await navigator.xr.isSessionSupported('immersive-vr');
        deviceInfo.webxr.sessions['immersive-ar'] = await navigator.xr.isSessionSupported('immersive-ar');
        deviceInfo.webxr.sessions['inline'] = await navigator.xr.isSessionSupported('inline');
      }
    } catch (error) {
      console.error('Error checking WebXR support:', error);
      deviceInfo.webxr.supported = false;
    }

    return deviceInfo;
  }

  /**
   * 获取浏览器名称
   * @returns 浏览器名称
   */
  private static getBrowserName(): string {
    const userAgent = navigator.userAgent;
    
    if (userAgent.indexOf('Chrome') > -1) return 'Chrome';
    if (userAgent.indexOf('Firefox') > -1) return 'Firefox';
    if (userAgent.indexOf('Safari') > -1) return 'Safari';
    if (userAgent.indexOf('Edge') > -1) return 'Edge';
    if (userAgent.indexOf('Opera') > -1 || userAgent.indexOf('OPR') > -1) return 'Opera';
    if (userAgent.indexOf('MSIE') > -1 || userAgent.indexOf('Trident') > -1) return 'Internet Explorer';
    
    return 'Unknown';
  }

  /**
   * 获取浏览器版本
   * @returns 浏览器版本
   */
  private static getBrowserVersion(): string {
    const userAgent = navigator.userAgent;
    let version = 'Unknown';
    
    // 提取浏览器版本
    const matches = userAgent.match(/(Chrome|Firefox|Safari|Edge|Opera|MSIE|Trident)\/(\d+\.\d+)/);
    if (matches && matches.length >= 3) {
      version = matches[2];
    }
    
    return version;
  }

  /**
   * 获取操作系统名称
   * @returns 操作系统名称
   */
  private static getOSName(): string {
    const userAgent = navigator.userAgent;
    
    if (userAgent.indexOf('Windows') > -1) return 'Windows';
    if (userAgent.indexOf('Macintosh') > -1) return 'macOS';
    if (userAgent.indexOf('Linux') > -1) return 'Linux';
    if (userAgent.indexOf('Android') > -1) return 'Android';
    if (userAgent.indexOf('iPhone') > -1 || userAgent.indexOf('iPad') > -1) return 'iOS';
    
    return 'Unknown';
  }

  /**
   * 获取操作系统版本
   * @returns 操作系统版本
   */
  private static getOSVersion(): string {
    const userAgent = navigator.userAgent;
    let version = 'Unknown';
    
    // 提取操作系统版本
    if (userAgent.indexOf('Windows') > -1) {
      const matches = userAgent.match(/Windows NT (\d+\.\d+)/);
      if (matches && matches.length >= 2) {
        version = matches[1];
      }
    } else if (userAgent.indexOf('Macintosh') > -1) {
      const matches = userAgent.match(/Macintosh; Intel Mac OS X (\d+_\d+(_\d+)?)/);
      if (matches && matches.length >= 2) {
        version = matches[1].replace(/_/g, '.');
      }
    } else if (userAgent.indexOf('Android') > -1) {
      const matches = userAgent.match(/Android (\d+\.\d+(\.\d+)?)/);
      if (matches && matches.length >= 2) {
        version = matches[1];
      }
    } else if (userAgent.indexOf('iPhone') > -1 || userAgent.indexOf('iPad') > -1) {
      const matches = userAgent.match(/OS (\d+_\d+(_\d+)?)/);
      if (matches && matches.length >= 2) {
        version = matches[1].replace(/_/g, '.');
      }
    }
    
    return version;
  }

  /**
   * 获取设备类型
   * @returns 设备类型
   */
  private static getDeviceType(): string {
    const userAgent = navigator.userAgent;
    const width = window.screen.width;
    
    if (userAgent.indexOf('Mobile') > -1) return 'Mobile';
    if (userAgent.indexOf('Tablet') > -1) return 'Tablet';
    if (width < 768) return 'Mobile';
    if (width < 1024) return 'Tablet';
    
    return 'Desktop';
  }

  /**
   * 获取设备型号
   * @returns 设备型号
   */
  private static getDeviceModel(): string {
    const userAgent = navigator.userAgent;
    
    // 提取设备型号
    if (userAgent.indexOf('iPhone') > -1) {
      const matches = userAgent.match(/iPhone\s*([^;]+)/);
      return matches && matches.length >= 2 ? matches[1] : 'iPhone';
    } else if (userAgent.indexOf('iPad') > -1) {
      const matches = userAgent.match(/iPad\s*([^;]+)/);
      return matches && matches.length >= 2 ? matches[1] : 'iPad';
    } else if (userAgent.indexOf('Android') > -1) {
      const matches = userAgent.match(/Android\s+\d+\.\d+;\s+([^;]+)/);
      return matches && matches.length >= 2 ? matches[1] : 'Android Device';
    }
    
    return 'Unknown';
  }

  /**
   * 检测设备是否支持触摸
   * @returns 是否支持触摸
   */
  static isTouchSupported(): boolean {
    return 'ontouchstart' in window || navigator.maxTouchPoints > 0;
  }

  /**
   * 检测设备是否支持VR
   * @returns 是否支持VR
   */
  static async isVRSupported(): Promise<boolean> {
    try {
      if (!navigator.xr) {
        return false;
      }
      return await navigator.xr.isSessionSupported('immersive-vr');
    } catch (error) {
      console.error('Error checking VR support:', error);
      return false;
    }
  }

  /**
   * 检测设备是否支持AR
   * @returns 是否支持AR
   */
  static async isARSupported(): Promise<boolean> {
    try {
      if (!navigator.xr) {
        return false;
      }
      return await navigator.xr.isSessionSupported('immersive-ar');
    } catch (error) {
      console.error('Error checking AR support:', error);
      return false;
    }
  }

  /**
   * 获取最佳的WebXR会话类型
   * @returns 最佳的WebXR会话类型
   */
  static async getBestWebXRSessionType(): Promise<string | null> {
    try {
      if (!navigator.xr) {
        return null;
      }

      // 优先检查VR
      if (await navigator.xr.isSessionSupported('immersive-vr')) {
        return 'immersive-vr';
      }

      // 然后检查AR
      if (await navigator.xr.isSessionSupported('immersive-ar')) {
        return 'immersive-ar';
      }

      // 最后检查内联
      if (await navigator.xr.isSessionSupported('inline')) {
        return 'inline';
      }

      return null;
    } catch (error) {
      console.error('Error getting best WebXR session type:', error);
      return null;
    }
  }
}
