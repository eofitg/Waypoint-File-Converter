# Waypoint-File-Converter
目前仅支持Voxel points转Xaeros ~~（虽然转了也没什么用）~~

## Tips:
1. 由于VoxelMap特殊的路径点存储方式，若在游戏内打开了某一路径点的跨纬度显示，那么该工具将无法识别该路径点的存储维度，只会暂时将其存储到主世界。
    
    ![Voxel路径点跨纬度显示](img/Voxel.png)

2. 目前只能提供Xaero转Voxel时的路径点颜色保留。因为Voxel几乎支持任意一种路径点颜色，而Xaero只支持16种颜色。

3. 诚然，可以读取Voxel的point颜色后，取一个Xaero与之最接近的颜色作为转换后的Xaero颜色，但是笨蛋迩欧实在没搞懂颜色通道方面的知识，所以放弃了。(目前如果Voxel转Xaero了话，颜色选取用的是自欺欺人的取随机值的方式)

4. 一个导出为Xaero‘s Map的文件夹（XaerosMiniMap）：

    ![XaerosMap](img/20220629204410.png)

---
